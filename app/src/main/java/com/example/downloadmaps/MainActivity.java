package com.example.downloadmaps;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IView {

	static final long BYTE_IN_GIGABYTE = 0x40000000L;
	static final String TAG_DATA = "data";
	private RetainedFragment retainedFragment;
	Toolbar toolbar;
	CountryListAdapter countryListAdapter;
	ArrayList<DownloadMap> downloadMapTasks = new ArrayList<>();
	LinkedList<Entry.EntryWithOffset> backStack = new LinkedList<>();
	RegionParser regionParser;
	private RecyclerView recyclerView;
	private LinearLayoutManager layoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		TextView tvFreeMemory = findViewById(R.id.tvFreeMemory);
		float freeMemoryF = MemoryInfo.getAvailableExternalMemorySize();
		freeMemoryF /= BYTE_IN_GIGABYTE;
		String freeMemory = String.format(Locale.getDefault(), "%s %.2f %s",
				getString(R.string.free), freeMemoryF, getString(R.string.gb));
		tvFreeMemory.setText(freeMemory);

		float totalMemoryF = MemoryInfo.getTotalExternalMemorySize();
		totalMemoryF /= BYTE_IN_GIGABYTE;
		ProgressBar progressBar = findViewById(R.id.progressBar);
		int progress = (int) ((1 - freeMemoryF / totalMemoryF) * 100);
		progressBar.setProgress(progress);

		recyclerView = findViewById(R.id.countryList);
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		regionParser = new RegionParser();
		InputStream regions = getResources().openRawResource(R.raw.regions);
		try {
			regionParser.parseInputStream(regions);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		countryListAdapter = new CountryListAdapter(this, regionParser.getFilteredList(null));

		if (recyclerView.getItemAnimator() != null) {
			((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		}

		FragmentManager fm = getFragmentManager();
		retainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_DATA);

		if (retainedFragment == null) {
			retainedFragment = new RetainedFragment();
			fm.beginTransaction().add(retainedFragment, TAG_DATA).commit();
			saveInRetainedFragment();
		} else {

			regionParser.setAllCountryList(retainedFragment.getCountryList());
			backStack = retainedFragment.getBackStack();
			downloadMapTasks = retainedFragment.getDownloadTaskList();
			for (DownloadMap t : downloadMapTasks) {
				t.setView(this);
			}
			if (!backStack.isEmpty()) {
				displayListHeader(backStack.peekFirst().entry);
				countryListAdapter.setItems(regionParser.getFilteredList(backStack.peekFirst().entry));
			}else {
				countryListAdapter.setItems(regionParser.getFilteredList(null));
			}
			countryListAdapter.notifyDataSetChanged();
		}
		recyclerView.setAdapter(countryListAdapter);
	}

	private void saveInRetainedFragment() {
		retainedFragment.setDownloadTaskList(downloadMapTasks);
		retainedFragment.setCountryList(regionParser.getAllCountryList());
		retainedFragment.setBackStack(backStack);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@Override
	public void onBackPressed() {
		if (!backStack.isEmpty()) {
			backPressed(backStack.pop());
			return;
		}
		super.onBackPressed();
	}

	private void backPressed(final Entry.EntryWithOffset entryWithOffset) {
		Entry entry = entryWithOffset.entry;
		countryListAdapter.setItems(regionParser.getFilteredList(entry.getRegion()));
		countryListAdapter.notifyDataSetChanged();
		displayListHeader(entry.getRegion());
		layoutManager.scrollToPositionWithOffset(entryWithOffset.position, entryWithOffset.offset);
	}

	private void displayListHeader(Entry entry) {
		if (entry != null) {
			changeListHeader(entry.getName(), true, View.GONE);
		} else {
			changeListHeader(getString(R.string.app_name), false, View.VISIBLE);
		}
	}

	private void changeListHeader(String title, boolean visibleHomeButton, int visible) {
		toolbar.setTitle(title);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(visibleHomeButton);
			getSupportActionBar().setDisplayShowHomeEnabled(visibleHomeButton);
		}
		findViewById(R.id.llFreeMemory).setVisibility(visible);
		findViewById(R.id.tvEurope).setVisibility(visible);
	}

	@Override
	public void subRegionClick(Entry entry) {
		if (entry != null) {
			if (regionParser.existSubRegion(entry)) {
				Entry.EntryWithOffset entryWithOffset
						= new Entry.EntryWithOffset(entry,layoutManager.findFirstVisibleItemPosition(),
						recyclerView.getChildAt(0).getTop());
				displayListHeader(entry);
				countryListAdapter.setItems(regionParser.getFilteredList(entry));
				countryListAdapter.notifyDataSetChanged();
				backStack.push(entryWithOffset);
			}
		}
	}

	@Override
	public void downloadMap(Entry entry) {
		entry.setLoadWaiting(true);
		Toast.makeText(this, entry.getFileName(), Toast.LENGTH_SHORT).show();
		DownloadMap downloadMap = new DownloadMap(this, entry);
		downloadMapTasks.add(downloadMap);
		downloadMap.execute();
		countryListAdapter.notifyDataSetChanged();
	}

	@Override
	public void updateProgress(Entry entry) {
		countryListAdapter.notifyItemChanged(countryListAdapter.getCountryList().indexOf(entry));
	}

	@Override
	public void cancelDownloadMap(final Entry entry) {
		final AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(MainActivity.this);
		alertDialog.setTitle(R.string.cancel);
		alertDialog.setMessage(R.string.cancel_file_download);
		alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				for (DownloadMap dm : downloadMapTasks) {
					if (dm.getFileName().equals(entry.getFileName())) {
						dm.cancel(true);
					}
				}
				entry.setLoadWaiting(false);
				entry.setDownloadProgress(0);
				Toast.makeText(getApplicationContext(), getString(R.string.cancelled),
						Toast.LENGTH_LONG).show();
				dialog.cancel();
			}
		});
		alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();
			}
		});
		alertDialog.setCancelable(true);
		alertDialog.show();
		countryListAdapter.notifyDataSetChanged();
	}

	@Override
	public void finishDownload(DownloadMap downloadMap) {
		downloadMapTasks.remove(downloadMap);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveInRetainedFragment();
	}
}
