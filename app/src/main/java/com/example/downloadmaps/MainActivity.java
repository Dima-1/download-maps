package com.example.downloadmaps;

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
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IView {

	static final long BYTE_IN_GIGABYTE = 0x40000000L;
	ProgressBar progressBar;
	CountryListAdapter countryListAdapter;
	ArrayList<DownloadMap> downloadMapTasks = new ArrayList<>();
	ArrayList<Entry> countryList;
	RegionParser regionParser;
	Toolbar toolbar;
	private RecyclerView recyclerView;

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
		progressBar = findViewById(R.id.progressBar);
		int progress = (int) ((1 - freeMemoryF / totalMemoryF) * 100);
		progressBar.setProgress(progress);

		recyclerView = findViewById(R.id.countryList);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		regionParser = new RegionParser();
		InputStream XmlFileInputStream = getResources().openRawResource(R.raw.regions);
		try {
			countryList = regionParser.setInputStream(XmlFileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		countryList = regionParser.getFilteredList(null);
		countryListAdapter = new CountryListAdapter(this, countryList);
		recyclerView.setAdapter(countryListAdapter);
		((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void subRegionClick(Entry entry) {
		if (entry != null) {
			if (regionParser.existSubRegion(entry)) {
				countryListAdapter.setItems(regionParser.getFilteredList(entry));
				toolbar.setTitle(entry.getName());
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setDisplayShowHomeEnabled(true);
				findViewById(R.id.llFreeMemory).setVisibility(View.GONE);
				findViewById(R.id.tvEurope).setVisibility(View.GONE);
				countryListAdapter.notifyDataSetChanged();
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
		entry.setLoadWaiting(false);
		entry.setDownloadProgress(0);
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
}
