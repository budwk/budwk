package com.budwk.starter.wechat.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import com.budwk.starter.wechat.bean.WxMaster;
import com.budwk.starter.wechat.impl.BasicWxHandler;
import com.budwk.starter.wechat.impl.WxApiImpl;
import com.budwk.starter.wechat.spi.WxAPI;
import com.budwk.starter.wechat.spi.WxHandler;

@Deprecated
public class WxContext {
	
	public static final String DEF = "default";

	public WxContext(){}
	
	protected Map<String, WxMaster> masters = new HashMap<String, WxMaster>();
	
	protected Map<String, WxAPI> apis = new HashMap<String, WxAPI>();
	
	protected Map<String, WxHandler> handlers = new HashMap<String, WxHandler>();
	
	public WxAPI getAPI(String openid) {
		if (openid == null)
			openid = DEF;
		return apis.get(openid);
	}
	
	public WxMaster get(String openid) {
		if (openid == null)
			openid = DEF;
		return masters.get(openid);
	}
	
	public void setApis(Map<String, WxAPI> apis) {
		this.apis = apis;
	}
	
	public void setMasters(Map<String, WxMaster> masters) {
		this.masters = masters;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setPath(String path) {
		PropertiesProxy pp = new PropertiesProxy(path);
		Map<String, Object> map = new LinkedHashMap(pp.toMap());
		if (pp.get("openid") != null) {
			String appid = pp.get("openid");
			WxMaster def = Lang.map2Object(map, WxMaster.class);
			masters.put(appid, def);
			apis.put(appid, new WxApiImpl(def));
			handlers.put(appid, new BasicWxHandler(def.getToken()));
		}
		for (Entry<String, Object> en : map.entrySet()) {
			String key = en.getKey();
			if (key.endsWith(".openid")) {
				key = key.substring(0, key.indexOf('.'));
				Map<String, Object> tmp = filter(map, key + ".", null, null, null);
				String openid = tmp.get("openid").toString();
				WxMaster one = Lang.map2Object(tmp, WxMaster.class);
				masters.put(openid, one);
				apis.put(openid, new WxApiImpl(one));
				handlers.put(openid, new BasicWxHandler(one.getToken()));
			}
		}
	}
	
	public String export() {
		StringBuilder sb = new StringBuilder();
		Map<String, WxMaster> map = new LinkedHashMap<String, WxMaster>(masters);
		for (Entry<String, WxMaster> en : map.entrySet()) {
			String prefix = null;
			if (DEF.equals(en.getKey())) {
				prefix = "";
			} else {
				prefix = en.getKey() + ".";
			}
			for (Entry<String, Object> _en : Lang.obj2map(en.getValue()).entrySet()) {
				sb.append(prefix).append(_en.getKey()).append('=').append(_en.getValue()).append('\n');
			}
		}
		return sb.toString();
	}
	
    /**
     * map对象浅过滤,返回值是一个新的map
     * @param source 原始的map对象
     * @param prefix 包含什么前缀,并移除前缀
     * @param include 正则表达式 仅包含哪些key(如果有前缀要求,则已经移除了前缀)
     * @param exclude 正则表达式 排除哪些key(如果有前缀要求,则已经移除了前缀)
     * @param keyMap 映射map, 原始key--目标key (如果有前缀要求,则已经移除了前缀)
     * @return 经过过滤的map,与原始map不是同一个对象
     */
    public static Map<String, Object> filter(Map<String, Object> source, String prefix, String include, String exclude, Map<String, String> keyMap) {
    	LinkedHashMap<String, Object> dst = new LinkedHashMap<String, Object>();
    	if (source == null || source.isEmpty())
    		return dst;
    	Pattern includePattern = include == null ? null : Pattern.compile(include);
    	Pattern excludePattern = exclude == null ? null : Pattern.compile(exclude);
    	
    	for (Entry<String, Object> en : source.entrySet()) {
    		String key = en.getKey();
    		if (prefix != null) {
    			if (key.startsWith(prefix))
    				key = key.substring(prefix.length());
    			else
    				continue;
    		}
    		if (includePattern != null && !includePattern.matcher(key).find())
    			continue;
    		if (excludePattern != null && excludePattern.matcher(key).find())
    			continue;
    		if (keyMap != null && keyMap.containsKey(key))
    			dst.put(keyMap.get(key), en.getValue());
    		else
    			dst.put(key, en.getValue());
		}
    	return dst;
    }
    
    public WxHandler getHandler(String openid) {
		return handlers.get(openid);
	}
}
