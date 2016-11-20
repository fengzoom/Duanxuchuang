package com.example.connect.duanxuchuang;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/10.
 */

public class DownInfo implements Serializable {
    private int id;
    private String url;
    private long length;
    private String filename;
    private int finished;

    public DownInfo() {
    }

    public DownInfo(int id, String url, long length, String filename, int finished) {
        this.id = id;
        this.url = url;
        this.length = length;
        this.filename = filename;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
}
