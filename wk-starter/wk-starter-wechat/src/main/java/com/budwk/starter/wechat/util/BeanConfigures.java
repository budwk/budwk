package com.budwk.starter.wechat.util;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.nutz.json.Json;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

public class BeanConfigures {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> T configure(T t, Object obj) {
		if (t == null)
			return t;
		if (obj == null)
			return t;
		Map<String, Object> map = null;
		if (obj instanceof Map) {
			map = (Map<String, Object>)obj;
		} else {
			map = asConfigureMap(obj);
		}
		Mirror mirror = Mirror.me(t);
		Mirror m2 = Mirror.me(obj);
		for(Field field : mirror.getFields()) {
			Object value = null;
			if (map != null)
				value = map.get(field.getName());
			else {
				try {
					value = m2.getValue(obj, field.getName());
				} catch (FailToGetValueException e) {
					// skip;
				}
			}
			if (value != null) {
				mirror.setValue(t, field.getName(), value);
			}
		}
		return t;
	}
	
	public static Map<String, Object> asConfigureMap(Object obj) {
		Map<String, Object> map = null;
		if (obj instanceof String) {
			try {
				map = Json.fromJsonAsMap(Object.class, (String)obj);
			} catch (Exception e) {
				try {
					Properties p = new Properties();
					p.load(new StringReader(obj.toString()));
					map = toMap(p);
				} catch (Exception e2) {
				}
			}
		} else if (obj instanceof Reader) {
			try {
				map = Json.fromJsonAsMap(Object.class, (Reader)obj);
			} catch (Exception e) {
				try {
					Properties p = new Properties();
					p.load((Reader)obj);
					map = toMap(p);
				} catch (Exception e2) {
				}
			}
		}
		return map;
	}
	
	public static Map<String, Object> toMap(Properties properties) {
		NutMap map = new NutMap();
		for (Entry<Object, Object> en : properties.entrySet()) {
			map.put(en.getKey().toString(), en.getValue());
		}
		if (map.isEmpty())
			return null;
		return map;
	}
}
