package com.wedesign.mediaplayer.Bluetooth;

/**
 * Created by NANA on 2016/5/14.
 */
public class MusicInfoEvent {
    public String name;
    public String artist;
    public int pos;
    public int total;
    public int duration;

    public MusicInfoEvent(String name, String artist, int duration, int pos, int total){
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.pos = pos;
        this.total = total;
    }
}
