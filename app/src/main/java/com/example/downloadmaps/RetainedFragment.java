package com.example.downloadmaps;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by DR
 * on 04.11.2019.
 */

public class RetainedFragment extends Fragment {

	private ArrayList<Entry> data;
	private LinkedList<Entry> backStack;
	ArrayList<DownloadMap> downloadMapTasks;

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

	public LinkedList<Entry> getBackStack() {
		return backStack;
	}

	public void setBackStack(LinkedList<Entry> backStack) {
		this.backStack = backStack;
	}

	public void setDownloadTaskList(ArrayList<DownloadMap> downloadMapTasks) {
		this.downloadMapTasks = downloadMapTasks;
	}

	public ArrayList<DownloadMap> getDownloadTaskList() {
		return downloadMapTasks;
	}
}