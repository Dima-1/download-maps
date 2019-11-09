package com.example.downloadmaps;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by DR
 * on 04.11.2019.
 */

public class RetainedFragment extends Fragment {

	private ArrayList<Entry> data;
	private LinkedList<Entry.EntryWithOffset> backStack = new LinkedList<>();
	private ArrayList<DownloadMap> downloadMapTasks = new ArrayList<>();
	private RegionParser regionParser;

	public RegionParser getRegionParser() {
		return regionParser;
	}

	public void setRegionParser(RegionParser regionParser) {
		this.regionParser = regionParser;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public void setCountryList(ArrayList<Entry> data) {
		this.data = data;
	}

	public ArrayList<Entry> getCountryList() {
		return data;
	}

	public LinkedList<Entry.EntryWithOffset> getBackStack() {
		return backStack;
	}

	public void setBackStack(LinkedList<Entry.EntryWithOffset> backStack) {
		this.backStack = backStack;
	}

	public void setDownloadTaskList(ArrayList<DownloadMap> downloadMapTasks) {
		this.downloadMapTasks = downloadMapTasks;
	}

	public ArrayList<DownloadMap> getDownloadTaskList() {
		return downloadMapTasks;
	}
}