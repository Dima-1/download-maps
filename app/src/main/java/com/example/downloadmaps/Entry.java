package com.example.downloadmaps;

/**
 * Created by DR
 * on 01.11.2019.
 */
class Entry {
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

	boolean isLoadWaiting() {
		return loadWaiting;
    }

	void setLoadWaiting(boolean loadWaiting) {
		this.loadWaiting = loadWaiting;
    }

	String getName() {
        return name;
    }

	void setName(String name) {
        this.name = name;
    }

	String getFileName() {
		return fileName;
    }

	void setFileName(String fileName) {
		this.fileName = fileName;
    }

	Entry getRegion() {
        return region;
    }

	/**
	 * Created by DR
	 * on 06.11.2019.
	 */
	static class EntryWithOffset {

		Entry entry;
		int position;
		int offset;

		EntryWithOffset(Entry entry, int position, int offset) {
			this.entry = entry;
			this.offset = offset;
			this.position = position;
		}
	}
}
