package com.example.downloadmaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	public static final long BYTE_IN_GIGABYTE = 0x40000000L;

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
		ProgressBar progressBar = findViewById(R.id.progressBar);
		int progress = (int) ((1 - freeMemoryF / totalMemoryF) * 100);
		progressBar.setProgress(progress);

		RecyclerView recyclerView = findViewById(R.id.countryList);

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		ArrayList<Entry> countryList = new ArrayList<Entry>(Arrays.asList(
				new Entry("denmark1", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark2", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark3", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark4", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark5", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark6", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark7", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark8", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark9", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark10", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark11", "Denmark_capital-region_2.obf.zip", null),
				new Entry("denmark12", "Denmark_capital-region_2.obf.zip", null)
		));
		CountryListAdapter countryListAdapter = new CountryListAdapter(getBaseContext(), countryList);
		recyclerView.setAdapter(countryListAdapter);
    }
}
