package com.budwk.starter.wechat.mvc;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class WxViewMaker implements ViewMaker {
	
	public View make(Ioc ioc, String type, String value) {
		if (!"wx".equals(type))
			return null;
		return WxView.me;
	}

}


