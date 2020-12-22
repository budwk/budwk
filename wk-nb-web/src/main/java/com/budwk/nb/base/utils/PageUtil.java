package com.budwk.nb.base.utils;

import org.nutz.lang.util.NutMap;

/**
 * @author wizzer(wizzer.cn) on 2018.09
 */
public class PageUtil {
    public static String getOrder(String key) {
        NutMap map = NutMap.NEW().addv("ascending", "asc").addv("descending", "desc");
        return map.getString(key);
    }
}
