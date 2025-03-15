package com.budwk.starter.wechat.spi;

import java.util.Enumeration;

/**
 *  微信会话
 *  @author wendal(wendal1985@gmail.com)
 *
 */
public interface WxSession { // 考虑继承HttpSession啦

	public String getId();
	public long getCreationTime();
	public Object getAttribute(String name);
	public void setAttribute(String name, Object value);
	public Enumeration<String> getAttributeNames();
	public long getLastAccessedTime();
	
	public void setMaxInactiveInterval(int interval);
	public int getMaxInactiveInterval();
	
	public void invalidate();
}
