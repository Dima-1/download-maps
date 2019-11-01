package com.example.downloadmaps;

/**
 * Created by DR
 * on 01.11.2019.
 */
public class Entry {
    String name;
    String URL;
    boolean loaded;
    Entry region;

    public Entry(String name, String URL, Entry region) {
        this.name = name;
        this.URL = URL;
        this.region = region;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Entry getRegion() {
        return region;
    }

    public void setRegion(Entry region) {
        this.region = region;
    }
}
