package com.example.downloadmaps;

public interface IView {

	void downloadMap(Entry entry);

	void updateProgress();

	void cancelDownloadMap(Entry entry);
}
