package com.example.downloadmaps;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class RegionParser {
	private static final String TAG = "RegionParser";
	private static final String ns = null;
	private static final String SUFFIX = "_europe_2.obf.zip";

	ArrayList setInputStream(InputStream xmlFileInputStream) throws IOException, XmlPullParserException {
		return parse(xmlFileInputStream);
	}

	private ArrayList parse(InputStream in) throws XmlPullParserException, IOException {

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private ArrayList readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {

		ArrayList entries = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "regions_list");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			Log.d(TAG, "readFeed: " + parser.getLineNumber() + " " + parser.getAttributeValue(null, "name"));
			if (name.equals("region")) {
				entries.addAll(readEurope(parser));
			}
		}
		Log.d(TAG, "readFeed: count ==== " + entries.size());
		return entries;
	}

	private ArrayList readEurope(XmlPullParser parser) throws IOException, XmlPullParserException {
		ArrayList entries = new ArrayList();
		parser.require(XmlPullParser.START_TAG, ns, "region");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String prefix = "";
			String name = parser.getName();
			if (name.equals("region")) {
				Log.d(TAG, "readEurope: " + createMapName(parser.getAttributeValue(null, "name")
						+ " " + createMapFileName(prefix, parser.getAttributeValue(null, "name"))));
				entries.add(new Entry(createMapName(parser.getAttributeValue(null, "name")),
						createMapFileName(prefix, parser.getAttributeValue(null, "name")), null));
				entries.addAll(readRegion(parser));
			}
		}
		return entries;
	}

	private ArrayList readRegion(XmlPullParser parser) throws IOException, XmlPullParserException {
		ArrayList entries = new ArrayList();
		parser.require(XmlPullParser.START_TAG, ns, "region");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			Log.d(TAG, "readRegion: " + parser.getLineNumber() + " " + parser.getAttributeValue(null, "name"));
			if (name.equals("region")) {
				entries.add(new Entry(parser.getAttributeValue(null, "name"), "", null));
				entries.addAll(readRegion(parser));
			}
		}
		return entries;
	}

	String createMapFileName(String prefix, String name) {
		if (!prefix.isEmpty()) {
			prefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1) + "_";
		} else {
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return prefix + name + SUFFIX;
	}

	private String createMapName(String name) {
		String[] splitName = name.split("-");
		StringBuilder nameBuilder = new StringBuilder();
		for (String n : splitName) {
			nameBuilder.append(n.substring(0, 1).toUpperCase()).append(n.substring(1)).append(" ");
		}
		name = nameBuilder.toString();
		name.trim();
		return name;
	}
}
