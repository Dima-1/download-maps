package com.example.downloadmaps;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RegionParser {
	InputStream xmlFileInputStream;
	private static final String ns = null;

	ArrayList<Entry> getRegions() {
		return new ArrayList<>(Arrays.asList(
				new Entry("Denmark", "Denmark_capital-region_europe_2.obf.zip", null),
				new Entry("Brest Region", "Belarus_brest_europe_2.obf.zip", null),
				new Entry("Homel region", "Belarus_gomel_europe_2.obf.zip", null),
				new Entry("Czechia", "Czech-republic_europe_2.obf.zip", null),
				new Entry("Romania", "Romania-region_europe_2.obf.zip", null),
				new Entry("Slovakia", "Slovakia_europe_2.obf.zip", null),
				new Entry("Ukraine", "Ukraine_europe_2.obf.zip", null),
				new Entry("Kyiv", "Ukraine_kiev-city_europe_2.obf.zip", null),
				new Entry("Auvergne-Rhône-Alpes", "France_auvergne-rhone-alpes_europe_2.obf.zip", null),
				new Entry("Auvergne-Rhône-Alpes Ain", "France_auvergne-rhone-alpes_ain_europe_2.obf.zip", null),
				new Entry("denmark9", "Denmark_capital-region_europe_2.obf.zip", null),
				new Entry("denmark10", "Denmark_capital-region_europe_2.obf.zip", null),
				new Entry("denmark11", "Denmark_capital-region_europe_2.obf.zip", null),
				new Entry("denmark12", "Denmark_capital-region_europe_2.obf.zip", null)
		));
	}

	public void setInputStream(InputStream xmlFileInputStream) throws IOException, XmlPullParserException {
		this.xmlFileInputStream = xmlFileInputStream;
		parse(xmlFileInputStream);
	}

	public List parse(InputStream in) throws XmlPullParserException, IOException {

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

	private List readFeed(XmlPullParser parser) {
		return new ArrayList();
	}
}
