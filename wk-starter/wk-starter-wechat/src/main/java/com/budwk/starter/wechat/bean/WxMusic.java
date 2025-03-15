package com.budwk.starter.wechat.bean;

public class WxMusic {
	private String title;
	private String description;
	private String musicUrl;
	private String hQMusicUrl;
	private String thumbMediaId;
	public WxMusic() {
	}
	public WxMusic(String title, String description, String musicUrl,
			String hQMusicUrl, String thumbMediaId) {
		super();
		this.title = title;
		this.description = description;
		this.musicUrl = musicUrl;
		this.hQMusicUrl = hQMusicUrl;
		this.thumbMediaId = thumbMediaId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMusicUrl() {
		return musicUrl;
	}
	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}
	public String getHQMusicUrl() {
		return hQMusicUrl;
	}
	public void setHQMusicUrl(String hQMusicUrl) {
		this.hQMusicUrl = hQMusicUrl;
	}
	public String getThumbMediaId() {
		return thumbMediaId;
	}
	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}
}
