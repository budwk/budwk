package com.budwk.starter.wechat.session.memory;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import com.budwk.starter.wechat.spi.WxSession;

public class MemoryWxSession implements WxSession {
	
	protected String id;
	protected long createTime;
	protected long lastAccessedTime;
	protected int maxInactiveInterval;
	protected Map<String, Object> attrs;
	protected boolean valid;
	
	protected MemorySessionManager manager;
	
	protected MemoryWxSession() {
		valid = true;
		attrs = new LinkedHashMap<String, Object>();
	}
	
	public MemoryWxSession(String id, MemorySessionManager manager) {
		this();
		this.id = id;
		this.manager = manager;
	}

	public String getId() {
		return id;
	}

	public long getCreationTime() {
		return createTime;
	}

	public Object getAttribute(String name) {
		checkValid();
		return attrs.get(name);
	}

	public void setAttribute(String name, Object value) {
		checkValid();
		attrs.put(name, value);
	}

	public Enumeration<String> getAttributeNames() {
		checkValid();
		return Collections.enumeration(attrs.keySet());
	}

	public void setMaxInactiveInterval(int interval) {
		checkValid();
		this.maxInactiveInterval = interval;
	}
	
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	protected void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	protected void setAttrs(Map<String, Object> attrs) {
		this.attrs = attrs;
	}

	public void invalidate() {
		valid = false;
		manager.remove(id);
	}
	
	protected void checkValid() {
		if (!valid)
			throw new IllegalStateException(String.format("session(%s) had been invalidate", id));
	}
}
