package com.wedesign.mediaplayer.vo;

/**
 * Created by NANA on 2016/4/29.
 */
public class PicInfo {
    long id;  //id
    String display_name;
    String data;  //图片的路径

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
}
