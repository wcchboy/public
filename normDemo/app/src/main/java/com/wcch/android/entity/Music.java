package com.wcch.android.entity;

import com.dd.plist.ASCIIPropertyListParser;

public class Music implements Comparable<Music> {
    private String album;
    private String artist;
    private int duration;
    private String name;
    private String path;
    private String pinyin;
    private boolean select;
    private long size;

    public boolean isSelect() {
        return this.select;
    }

    public void setSelect(boolean z) {
        this.select = z;
    }

    public Music(String str, String str2, String str3, String str4, long j, int i) {
        this.name = str;
        this.path = str2;
        this.album = str3;
        this.artist = str4;
        this.size = j;
        this.duration = i;
        this.pinyin = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public String getPinyin() {
        return this.pinyin;
    }

    public void setPinyin(String str) {
        this.pinyin = str;
    }

    public int compareTo(Music music) {
        return this.pinyin.compareTo(music.getPinyin());
    }

    public String toString() {
        return "Music{name='" + this.name + '\'' + ", path='" + this.path + '\'' + ", album='" + this.album + '\'' + ", artist='" + this.artist + '\'' + ", size=" + this.size + ", duration=" + this.duration + ", pinyin='" + this.pinyin + '\'' + ASCIIPropertyListParser.DICTIONARY_END_TOKEN;
    }
}
