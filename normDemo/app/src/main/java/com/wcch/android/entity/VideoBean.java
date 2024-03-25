package com.wcch.android.entity;

public class VideoBean {
    public String video_displayName;
    public int video_duration;
    public int video_id;
    public String video_path;
    public String video_thumb_path;
    public String video_title;

    public String getVideo_thumb_path() {
        return this.video_thumb_path;
    }

    public void setVideo_thumb_path(String str) {
        this.video_thumb_path = str;
    }

    public int getVideo_id() {
        return this.video_id;
    }

    public void setVideo_id(int i) {
        this.video_id = i;
    }

    public String getVideo_path() {
        return this.video_path;
    }

    public void setVideo_path(String str) {
        this.video_path = str;
    }

    public String getVideo_displayName() {
        return this.video_displayName;
    }

    public void setVideo_displayName(String str) {
        this.video_displayName = str;
    }

    public String getVideo_title() {
        return this.video_title;
    }

    public void setVideo_title(String str) {
        this.video_title = str;
    }

    public int getVideo_duration() {
        return this.video_duration;
    }

    public void setVideo_duration(int i) {
        this.video_duration = i;
    }
}
