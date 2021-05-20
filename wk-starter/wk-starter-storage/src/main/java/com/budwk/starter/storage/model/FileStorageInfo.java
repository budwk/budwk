package com.budwk.starter.storage.model;

import org.nutz.lang.util.NutMap;

public class FileStorageInfo extends NutMap {
    private static final long serialVersionUID = 6028219481832546429L;

    public void setPath(String path) {
        this.put("path", path);
    }

    public String getPath() {
        return this.getString("path");
    }

    public void setType(String type) {
        this.put("type", type);
    }

    public String getType() {
        return this.getString("type");
    }

    public void setSize(long size) {
        this.put("size", size);
    }

    public long getSize() {
        return this.getLong("size");
    }

    public void setFileName(String filename) {
        this.put("filename", filename);
    }

    public String getFileName() {
        return this.getString("filename");
    }

    public void setUrl(String url) {
        this.put("url", url);
    }

    public String getUrl() {
        return this.getString("url");
    }

}
