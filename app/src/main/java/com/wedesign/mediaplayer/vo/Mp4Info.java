package com.wedesign.mediaplayer.vo;

/**
 * Created by NANA on 2016/4/23.
 */
public class Mp4Info {
    long id;
    String display_name;
    String data;
    long duration = 0;
    long video_item_progressed = 0;

    public long getVideo_item_progressed() {
        return video_item_progressed;
    }

    public void setVideo_item_progressed(long video_item_progressed) {
        this.video_item_progressed = video_item_progressed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    @Override
    public String toString() {
        return "Mp4Info{" +
                "id=" + id +
                ", display_name='" + display_name + '\'' +
                ", data='" + data + '\'' +
                ", duration=" + duration +
                '}';
    }
}
