package com.example.downloadmaps;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class RegionParser {
	private static final String TAG = "RegionParser";
	private static final String ns = null;
	private static final String SUFFIX = "_europe_2.obf.zip";
	private ArrayList<Entry> arrayList = new ArrayList<>();

	ArrayList setInputStream(InputStream xmlFileInputStream) throws IOException, XmlPullParserException {
		return parse(xmlFileInputStream);
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
			e.setName(createName(e.getName()));
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
				Entry region = new Entry((parser.getAttributeValue(null, "name")), mapFileName, null);
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
				Entry subRegion = new Entry(parser.getAttributeValue(null, "name"), mapFileName, parent);
				if (!(parser.getAttributeValue(null, "type") != null
						&& !parser.getAttributeValue(null, "type").equals("map"))) {
					entries.add(subRegion);
				}
				entries.addAll(readSubRegion(parser,
						prefix + "_" + parser.getAttributeValue(null, "name"), subRegion));
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

	private String createName(String name) {
		String[] splitName = name.split("-");
		StringBuilder nameBuilder = new StringBuilder();
		for (String n : splitName) {
			nameBuilder.append(n.substring(0, 1).toUpperCase()).append(n.substring(1)).append(" ");
		}
		name = nameBuilder.toString().trim();
		return name;
	}

	boolean existSubRegion(Entry entry) {
		for (int i = 0; i < arrayList.size(); i++)
			if (entry.equals(arrayList.get(i).getRegion()))
				return true;
		return false;
	}
}
