package com.budwk.starter.wechat.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;
import com.budwk.starter.wechat.bean.WxOutMsg;
import com.budwk.starter.wechat.util.Wxs;

public class WxView implements View {
	
	public static final View me = new WxView();
	
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		if (obj == null) {
			return;
		}
		Wxs.asXml(resp.getWriter(), (WxOutMsg) obj);
	}
}