package com.budwk.starter.common.page;

import org.nutz.lang.util.NutMap;

/**
 * @author wizzer@qq.com
 */
public class PageUtil {
    public static String getOrder(String key) {
        NutMap map = NutMap.NEW().addv("ascending", "asc").addv("descending", "desc");
        return map.getString(key);
    }
}
