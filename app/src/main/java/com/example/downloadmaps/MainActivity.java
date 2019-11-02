package com.example.downloadmaps;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IView {

	static final long BYTE_IN_GIGABYTE = 0x40000000L;
	ProgressBar progressBar;
	CountryListAdapter countryListAdapter;
	ArrayList<DownloadMap> downloadMapTasks = new ArrayList<>();
	ArrayList<Entry> countryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

		RecyclerView recyclerView = findViewById(R.id.countryList);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		RegionParser regionParser = new RegionParser();
		countryList = regionParser.getRegions();
		countryListAdapter = new CountryListAdapter(this, countryList);
		recyclerView.setAdapter(countryListAdapter);
		((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		InputStream XmlFileInputStream = getResources().openRawResource(R.raw.regions);
//		regionParser.setInputStream(XmlFileInputStream);

    }

	@Override
	public void downloadMap(Entry entry) {
		Toast.makeText(this, entry.getFileName(), Toast.LENGTH_SHORT).show();
		DownloadMap downloadMap = new DownloadMap(this, entry);
		downloadMapTasks.add(downloadMap);
		downloadMap.execute();
	}


	@Override
	public void updateProgress(Entry entry) {
		countryListAdapter.setItems(countryList);
		countryListAdapter.notifyItemChanged(countryList.indexOf(entry));
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
	}


	@Override
	public void finishDownload(DownloadMap downloadMap) {
		downloadMapTasks.remove(downloadMap);
	}
}
