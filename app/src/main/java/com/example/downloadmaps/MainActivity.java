package com.example.downloadmaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

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


    }
}
