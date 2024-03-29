package com.example.downloadmaps;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.downloadmaps.DownloadMap.MAP_FOLDER;

class RegionParser extends AsyncTask<InputStream, Void, ArrayList> {
	private static final String TAG = "RegionParser";
	private static final String ns = null;
	private static final String SUFFIX = "_europe_2.obf.zip";
	private ArrayList<Entry> arrayList = new ArrayList<>();
	private IView view;

	RegionParser(IView view) {
		this.view = view;
	}

	void setView(IView view) {
		this.view = view;
	}

	ArrayList<Entry> getFilteredList(Entry parent) {
		ArrayList<Entry> filteredArray = new ArrayList<>();
		for (Entry e : arrayList) {
			if (e.getRegion() == parent) {
				filteredArray.add(e);
			}
		}
		Comparator<Entry> comparator = new Comparator<Entry>() {

			public int compare(Entry entry1, Entry entry2) {
				return entry1.getName().compareTo(entry2.getName());
			}
		};
		Collections.sort(filteredArray, comparator);
		return filteredArray;
	}

	private ArrayList<Entry> parse(InputStream in) throws XmlPullParserException, IOException {

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			arrayList = readFeed(parser);
			return arrayList;
		} finally {
			in.close();
		}
	}

	private ArrayList<Entry> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {

		ArrayList<Entry> entries = new ArrayList<>();

		parser.require(XmlPullParser.START_TAG, ns, "regions_list");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			Log.d(TAG, "readFeed: " + parser.getLineNumber() + " " + parser.getAttributeValue(null, "name"));
			if (name.equals("region")) {
				entries.addAll(readRegion(parser));
			}
		}
		Log.d(TAG, "readFeed: count: " + entries.size());
		createNameAndMapName(entries);
		return entries;
	}

	private void createNameAndMapName(ArrayList<Entry> entries) {
		for (Entry e : entries) {
			if (!e.getFileName().isEmpty()) {
				e.setFileName(createMapFileName(e.getFileName()));
			}
			markExistedFiles(e);
		}
	}

	private void markExistedFiles(Entry e) {
		if (!e.getFileName().isEmpty()) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + MAP_FOLDER + File.separator + e.getFileName());
			e.setDownloadProgress(file.exists() ? 100 : 0);
		}
	}

	private ArrayList<Entry> readRegion(XmlPullParser parser) throws IOException, XmlPullParserException {
		ArrayList<Entry> entries = new ArrayList<>();
		parser.require(XmlPullParser.START_TAG, ns, "region");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("region")) {
				String mapFileName = "";
				if (parser.getAttributeValue(null, "map") == null || !isAttrMapEqualsNo(parser)) {
					mapFileName = parser.getAttributeValue(null, "name");
				}
				Log.d(TAG, "readRegion: " + (parser.getAttributeValue(null, "name") + " - " + mapFileName));
				Entry region = new Entry(createName(parser), mapFileName, null);
				entries.add(region);
				entries.addAll(readSubRegion(parser, parser.getAttributeValue(null, "name"), region));
			}
		}
		return entries;
	}

	private ArrayList<Entry> readSubRegion(XmlPullParser parser, String prefix, Entry parent)
			throws IOException, XmlPullParserException {

		ArrayList<Entry> entries = new ArrayList<>();
		parser.require(XmlPullParser.START_TAG, ns, "region");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("region")) {
				String mapFileName = "";
				if (parser.getAttributeValue(null, "map") == null || !isAttrMapEqualsNo(parser)) {
					mapFileName = prefix + "_" + parser.getAttributeValue(null, "name");
				}
				Log.d(TAG, "readSubRegion: " + parser.getLineNumber()
						+ " " + parser.getAttributeValue(null, "name")
						+ " = " + mapFileName + " __^"
						+ ((parent != null) ? parent.getName() : "--"));
				Entry subRegion = new Entry(createName(parser), mapFileName, parent);
				if (!(parser.getAttributeValue(null, "type") != null
						&& !parser.getAttributeValue(null, "type").equals("map"))) {
					entries.add(subRegion);
				}

				ArrayList<Entry> subRegionEntries = readSubRegion(parser,
						prefix + "_" + parser.getAttributeValue(null, "name"), subRegion);
				if (subRegionEntries.isEmpty() && isAttrMapEqualsNo(parser)) {
					entries.remove(subRegion);
				}
				entries.addAll(subRegionEntries);
			}
		}
		return entries;
	}

	private boolean isAttrMapEqualsNo(XmlPullParser parser) {
		return parser.getAttributeValue(null, "map") != null
				&& parser.getAttributeValue(null, "map").equals("no");
	}

	private String createMapFileName(String name) {
	    name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name + SUFFIX;
	}

	private String createName(XmlPullParser parser) {
		String name = parser.getAttributeValue(null, "name");
		String translate = parser.getAttributeValue(null, "translate");
		if (translate != null) {
			//check absent semicolon for non translated translation
			int indexOfSemicolon = translate.contains(";") ? translate.indexOf(";") : translate.length();
			name = translate.substring(0, indexOfSemicolon);
			name = name.substring(name.contains("=") ? name.indexOf("=") + 1 : 0);
		} else {
			String[] splitName = name.split("-");
			StringBuilder nameBuilder = new StringBuilder();
			for (String n : splitName) {
				nameBuilder.append(n.substring(0, 1).toUpperCase()).append(n.substring(1)).append(" ");
			}
			name = nameBuilder.toString().trim();
		}
		return name;
	}

	boolean existSubRegion(Entry entry) {
		for (int i = 0; i < arrayList.size(); i++)
			if (entry.equals(arrayList.get(i).getRegion()))
				return true;
		return false;
	}

	ArrayList<Entry> getAllCountryList() {
		return arrayList;
	}

	void setAllCountryList(ArrayList<Entry> arrayList) {
		this.arrayList = arrayList;
	}

	@Override
	protected ArrayList<Entry> doInBackground(InputStream[] input) {

		try {
			return parse(input[0]);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList arrayList) {
		super.onPostExecute(arrayList);
		view.parsingFinished();
	}
}
