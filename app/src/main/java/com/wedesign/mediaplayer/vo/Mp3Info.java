package com.wedesign.mediaplayer.vo;

/**
 * Created by Administrator on 2016/2/29.
 */
public class Mp3Info {
    private long id;//歌曲id
    private long mp3InfoId;
    private String tittle;//歌名
    private String artist;//艺术家
    private String album;//专辑
    private long albumId;//专辑id
    private long duration;//歌曲时长
    private long size;//大小
    private String url;//路径
    private String displayName;//显示的名称

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public long getMp3InfoId() {
        return mp3InfoId;
    }

    public void setMp3InfoId(long mp3InfoId) {
        this.mp3InfoId = mp3InfoId;
    }
    public long getId () {
         return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "Mp3Info{" +
                "id=" + id +
                ", mp3InfoId=" + mp3InfoId +
                ", tittle='" + tittle + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                '}';
    }
}
