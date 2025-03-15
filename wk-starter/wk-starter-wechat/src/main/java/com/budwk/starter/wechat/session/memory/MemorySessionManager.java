package com.budwk.starter.wechat.session.memory;

import java.util.concurrent.ConcurrentHashMap;

import com.budwk.starter.wechat.session.AbstractWxSessionManager;
import com.budwk.starter.wechat.spi.WxSession;

public class MemorySessionManager extends AbstractWxSessionManager {

	/**
	 * 默认超时为2天
	 */
	public static long DEF_TIMEOUT = 2*24*60;

	protected ConcurrentHashMap<String, MemoryWxSession> sessions = new ConcurrentHashMap<String, MemoryWxSession>();

	public WxSession getSession(String id, boolean create) {
		String sessionId = id;
		MemoryWxSession session = sessions.get(sessionId);
		if (session != null) {
			int maxInterval = session.getMaxInactiveInterval();
			if (maxInterval < 1) // 永不过期
				return session;
			long interval = (System.currentTimeMillis() - session.getLastAccessedTime()) / 1000 / 60;
			if (maxInterval > interval) {
				session.setLastAccessedTime(System.currentTimeMillis());
				return session;
			}
			session = null;
		}
		synchronized (sessions) {
			session = sessions.get(sessionId);
			if (session == null) {
				session = new MemoryWxSession(sessionId, this);
				session.setCreateTime(System.currentTimeMillis());
				session.setLastAccessedTime(System.currentTimeMillis());
				sessions.put(sessionId, session);
				return session;
			}
		}
		return session;
	}
	
	void remove(String id) {
		sessions.remove(id);
	}

}
