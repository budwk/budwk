package com.budwk.app.sys.providers;

import java.util.List;
import java.util.Map;

import com.budwk.app.sys.models.Sys_area;
import org.nutz.lang.util.NutMap;

public interface ISysAreaProvider {
    
    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    List<Sys_area> getSubListByCode(String code);


    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    NutMap getSubMapByCode(String code);
}
