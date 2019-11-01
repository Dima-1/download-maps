package com.example.downloadmaps;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DR
 * on 01.11.2019.
 */

class DownloadMap extends AsyncTask<Entry, Integer, String> {
	private static final String BASE_URL = "http://download.osmand.net/download.php?standard=yes&file=";
	private static final String MAP_FOLDER = "maps";
	private IView iView;

	DownloadMap(IView iView) {
		this.iView = iView;
	}

	private String fileName;
	private String folderName;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Entry... params) {
		int count;
		fileName = params[0].getFileName();
		try {
			URL url = new URL(BASE_URL + fileName);
			URLConnection connection = url.openConnection();
			connection.connect();
			int lengthOfFile = connection.getContentLength();
			InputStream input = new BufferedInputStream(url.openStream(), 8192);

			folderName = Environment.getExternalStorageDirectory()
					+ File.separator + MAP_FOLDER + File.separator;

			File directory = new File(folderName);

			if (!directory.exists()) {
				directory.mkdirs();
			}

			OutputStream output = new FileOutputStream(folderName + fileName);

			byte[] data = new byte[1024];

			long total = 0;
			int threshold = 0;
			while ((count = input.read(data)) != -1) {
				total += count;

				int downloadProgress = (int) ((total * 100) / lengthOfFile);
				if (downloadProgress > threshold) {
					threshold += 4;
					params[0].setDownloadProgress(downloadProgress);
					publishProgress();
				}

				output.write(data, 0, count);
			}
			params[0].setDownloadProgress(100);
			publishProgress();
			output.flush();
			output.close();
			input.close();

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}
		return folderName + fileName;
	}

	protected void onProgressUpdate(Integer... progress) {
		iView.updateProgress();
	}

}
