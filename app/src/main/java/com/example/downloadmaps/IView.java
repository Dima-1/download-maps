package com.example.downloadmaps;

public interface IView {

	void downloadMap(Entry entry);

	void updateProgress(Entry entry);

	void cancelDownloadMap(Entry entry);

	void finishDownload(DownloadMap downloadMap);
}
