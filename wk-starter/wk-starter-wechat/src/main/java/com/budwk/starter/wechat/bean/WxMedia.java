package com.budwk.starter.wechat.bean;

import java.io.InputStream;

public class WxMedia {

	private String id;
	private long size;
	private String contentType;
	private transient InputStream stream;
	
	public WxMedia() {
	}
	
	
	
	public WxMedia(String id, long size, String contentType) {
		super();
		this.id = id;
		this.size = size;
		this.contentType = contentType;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}
