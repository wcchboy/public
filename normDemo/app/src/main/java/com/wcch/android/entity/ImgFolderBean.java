package com.wcch.android.entity;

import com.dd.plist.ASCIIPropertyListParser;

import org.eclipse.paho.client.mqttv3.MqttTopic;

public class ImgFolderBean {
    private int count;
    private String dir;
    private String fistImgPath;
    private String name;

    public String getDir() {
        return this.dir;
    }

    public void setDir(String str) {
        this.dir = str;
        this.name = str.substring(str.lastIndexOf(MqttTopic.TOPIC_LEVEL_SEPARATOR) + 1);
    }

    public String getFistImgPath() {
        return this.fistImgPath;
    }

    public void setFistImgPath(String str) {
        this.fistImgPath = str;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public ImgFolderBean() {
    }

    public ImgFolderBean(String str, String str2, String str3, int i) {
        this.dir = str;
        this.fistImgPath = str2;
        this.name = str3;
        this.count = i;
    }

    public String toString() {
        return "ImgFolderBean{dir='" + this.dir + '\'' + ", fistImgPath='" + this.fistImgPath + '\'' + ", name='" + this.name + '\'' + ", count=" + this.count + ASCIIPropertyListParser.DICTIONARY_END_TOKEN;
    }
}
