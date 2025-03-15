package com.budwk.starter.wechat.bean;

public class WxVoice {
	
	private String mediaId;

	public WxVoice() {
	}

	public WxVoice(String mediaId) {
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
