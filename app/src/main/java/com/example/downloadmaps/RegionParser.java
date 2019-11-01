package com.example.downloadmaps;

import java.util.ArrayList;
import java.util.Arrays;

class RegionParser {
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
}
