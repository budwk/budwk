package com.budwk.nb.commons.utils;

import org.nutz.lang.Lang;

import java.util.*;

/**
 * 签名工具类
 * @author wizzer(wizzer.cn) on 2018/6/28.
 */
public class SignUtil {

    /**
     * 排序生成待签名字符串
     *
     * @param appkey appkey
     * @param params 参数对象
     * @return
     */
    public static String createSign(String appkey, Map<String, Object> params) {
        Map<String, Object> map = sortMapByKey(params);
        StringBuffer sb = new StringBuffer();
        Set<String> keySet = map.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext()) {
            String k = it.next();
            String v = (String) map.get(k);
            if (null != v && !"".equals(v)
                    && !"sign".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("appkey=" + appkey);
        String sign = Lang.md5(sb.toString());
        return sign;
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map map对象
     * @return
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<>(
                new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }


    static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
