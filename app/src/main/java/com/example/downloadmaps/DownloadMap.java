package com.example.downloadmaps;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DR
 * on 01.11.2019.
 */

class DownloadMap extends AsyncTask<Void, Integer, String> {
	private static final String BASE_URL = "http://download.osmand.net/download.php?standard=yes&file=";
	static final String MAP_FOLDER = "maps";
	private static final int INPUT_BUFFER_SIZE = 8192;
	private IView view;

	private String fileName;
	private String folderName;
	private Entry entry;

	DownloadMap(IView view, Entry entry) {
		this.view = view;
		this.entry = entry;
		fileName = entry.getFileName();
	}

	void setView(IView view) {
		this.view = view;
	}

	String getFileName() {
		return fileName;
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
		view.updateProgress(entry);
	}

	@Override
	protected String doInBackground(Void... params) {
		int numberOfBytesRead;
		fileName = entry.getFileName();
		try {
			URL url = new URL(BASE_URL + fileName);
			URLConnection connection = url.openConnection();
			connection.connect();
			int lengthOfFile = connection.getContentLength();
			InputStream input = new BufferedInputStream(url.openStream(), INPUT_BUFFER_SIZE);

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
			while ((numberOfBytesRead = input.read(data)) != -1) {
				if (isCancelled()) {
					entry.setDownloadProgress(0);
					closeStream(input, output);
					File tempFile = new File(folderName + fileName);
					tempFile.delete();
					return "cancel";
				}
				total += numberOfBytesRead;

				int downloadProgress = (int) ((total * 100) / lengthOfFile);
				if (downloadProgress > threshold) {
					threshold += 4;
					entry.setDownloadProgress(downloadProgress);
					publishProgress();
				}

				output.write(data, 0, numberOfBytesRead);
			}
			entry.setDownloadProgress(100);
			closeStream(input, output);

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}
		return folderName + fileName;
	}

	private void closeStream(InputStream input, OutputStream output) throws IOException {
		publishProgress();
		output.flush();
		output.close();
		input.close();
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		view.finishDownload(this);
		view.updateProgress(entry);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		entry.setDownloadProgress(0);
		view.finishDownload(this);
		view.updateProgress(entry);

	}
	protected void onProgressUpdate(Integer... progress) {
		view.updateProgress(entry);
	}

}
