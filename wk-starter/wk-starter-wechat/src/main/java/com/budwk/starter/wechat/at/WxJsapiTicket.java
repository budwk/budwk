package com.budwk.starter.wechat.at;

public class WxJsapiTicket {

	protected String ticket;

	protected int expires;
	
	protected long lastCacheTimeMillis;

	public WxJsapiTicket(String ticket, int expires, long lastCacheTimeMillis) {
		super();
		this.ticket = ticket;
		this.expires = expires;
		this.lastCacheTimeMillis = lastCacheTimeMillis;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpires() {
		return expires;
	}

	public void setExpires(int expires) {
		this.expires = expires;
	}

	public long getLastCacheTimeMillis() {
		return lastCacheTimeMillis;
	}

	public void setLastCacheTimeMillis(long lastCacheTimeMillis) {
		this.lastCacheTimeMillis = lastCacheTimeMillis;
	}

}
