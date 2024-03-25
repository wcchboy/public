package com.wcch.android.entity;

import com.dd.plist.ASCIIPropertyListParser;

public class FileBean {
    public long date_modified;
    public int iconId;
    public String path;
    public boolean select;
    public long size;

    public FileBean(String str, long j, int i, long j2) {
        this.path = str;
        this.size = j;
        this.iconId = i;
        this.date_modified = j2;
    }

    public String toString() {
        return "FileBean{path='" + this.path + '\'' + ", size=" + this.size + ", date_modified=" + this.date_modified + ASCIIPropertyListParser.DICTIONARY_END_TOKEN;
    }
}
