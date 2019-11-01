package com.example.downloadmaps;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IView {

	static final long BYTE_IN_GIGABYTE = 0x40000000L;
	ProgressBar progressBar;
	CountryListAdapter countryListAdapter;
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

		countryList = new RegionParser().getRegions();
		countryListAdapter = new CountryListAdapter(this, countryList);
		recyclerView.setAdapter(countryListAdapter);
    }

	@Override
	public void downloadMap(Entry entry) {
		Toast.makeText(this, entry.getFileName(), Toast.LENGTH_SHORT).show();
		new DownloadMap(this).execute(entry);
	}

	@Override
	public void updateProgress() {
		countryListAdapter.setItems(countryList);
		countryListAdapter.notifyDataSetChanged();
	}

	@Override
	public void cancelDownloadMap(Entry entry) {
		final AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(MainActivity.this);
		alertDialog.setTitle("Cancel");
		alertDialog.setMessage("Cancel file download ?");
		alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				Toast.makeText(getApplicationContext(), "Cancelled",
						Toast.LENGTH_LONG).show();
				dialog.cancel();
			}
		});
		alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();
			}
		});
		alertDialog.setCancelable(true);
		alertDialog.show();
	}
}
