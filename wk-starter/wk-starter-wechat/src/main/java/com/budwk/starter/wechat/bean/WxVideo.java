package com.budwk.starter.wechat.bean;

public class WxVideo {

	private String mediaId;
	private String title;
	private String description;
    private String thumb_media_id;
	public WxVideo() {
	}
	
	public WxVideo(String mediaId, String title, String description) {
		super();
		this.mediaId = mediaId;
		this.title = title;
		this.description = description;
	}

	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
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

    public String getThumb_media_id() {
        return thumb_media_id;
    }

    public void setThumb_media_id(String thumb_media_id) {
        this.thumb_media_id = thumb_media_id;
    }
}
