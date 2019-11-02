package com.example.downloadmaps;

/**
 * Created by DR
 * on 01.11.2019.
 */
public class Entry {
	private String name;
	private String fileName;
	private boolean loadWaiting;
	private int downloadProgress;
	private Entry region;

	int getDownloadProgress() {
		return downloadProgress;
	}

	void setDownloadProgress(int downloadProgress) {
		this.downloadProgress = downloadProgress;
	}


	Entry(String name, String fileName, Entry region) {
        this.name = name;
		this.fileName = fileName;
        this.region = region;
    }

	public boolean isLoadWaiting() {
		return loadWaiting;
    }

	public void setLoadWaiting(boolean loadWaiting) {
		this.loadWaiting = loadWaiting;
    }

	String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	String getFileName() {
		return fileName;
    }

	public void setFileName(String fileName) {
		this.fileName = fileName;
    }

    public Entry getRegion() {
        return region;
    }

    public void setRegion(Entry region) {
        this.region = region;
    }
}
