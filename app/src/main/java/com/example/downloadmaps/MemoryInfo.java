package com.example.downloadmaps;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class MemoryInfo {
	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		return getAvailableMemorySize(path);
	}

	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		return getTotalMemorySize(path);
	}

	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			return getAvailableMemorySize(path);
		} else {
			return -1;
		}
	}

	public static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			return getTotalMemorySize(path);
		} else {
			return -1;
		}
	}

	private static long getAvailableMemorySize(File path) {
		long blockSize;
		long availableBlocks;
		StatFs stat = new StatFs(path.getPath());
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}
		return availableBlocks * blockSize;
	}

	private static long getTotalMemorySize(File path) {
		StatFs stat = new StatFs(path.getPath());
		long blockSize;
		long totalBlocks;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = stat.getBlockSizeLong();
			totalBlocks = stat.getBlockCountLong();
		} else {
			blockSize = stat.getBlockSize();
			totalBlocks = stat.getBlockCount();
		}
		return totalBlocks * blockSize;
	}
}
