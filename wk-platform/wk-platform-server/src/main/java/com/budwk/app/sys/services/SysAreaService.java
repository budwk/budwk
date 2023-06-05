package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_area;
import com.budwk.starter.database.service.BaseService;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author wizzer.cn
 */
public interface SysAreaService extends BaseService<Sys_area> {

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
     * @param filedName 字段名
     * @param code 标识
     * @return
     */
    List<Sys_area> getSubListByCode(String filedName, String code);

    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    NutMap getSubMapByCode(String code);

    /**
     * 保存行政区划
     *
     * @param area 行政区划
     * @param pid  父ID
     */
    void save(Sys_area area, String pid);

    /**
     * 级联删除数据
     *
     * @param area 行政区划
     */
    void deleteAndChild(Sys_area area);

    /**
     * 清空缓存
     */
    void cacheClear();
}
