package com.budwk.starter.wechat.bean;

public class WxImage {

	private String mediaId;
	
	public WxImage() {
	}
	
	

	public WxImage(String mediaId) {
		super();
		this.mediaId = mediaId;
	}



	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}
