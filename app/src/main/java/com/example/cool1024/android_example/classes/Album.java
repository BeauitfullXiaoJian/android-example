package com.example.cool1024.android_example.classes;

public class Album extends BaseData {
    private String albumTitle;
    private String albumThumbUrl;
    private int pictureNum;

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumThumbUrl() {
        return albumThumbUrl;
    }

    public void setAlbumThumbUrl(String albumThumbUrl) {
        this.albumThumbUrl = albumThumbUrl;
    }

    public int getPictureNum() {
        return pictureNum;
    }

    public void setPictureNum(int pictureNum) {
        this.pictureNum = pictureNum;
    }
}
