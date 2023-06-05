package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_dict;
import com.budwk.starter.database.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
public interface SysDictService extends BaseService<Sys_dict> {

    /**
     * 通过code获取name
     *
     * @param code 字典标识
     * @return
     */
    String getNameByCode(String code);

    /**
     * 通过id获取name
     *
     * @param id ID
     * @return
     */
    String getNameById(String id);

    /**
     * 通过树PATH获取子级
     *
     * @param path 路径
     * @return
     */
    List<Sys_dict> getSubListByPath(String path);

    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    List<Sys_dict> getSubListByCode(String code);

    /**
     * 通过code获取子级
     *
     * @param filedName 字段名
     * @param code      标识
     * @return
     */
    List<Sys_dict> getSubListByCode(String filedName, String code);

    /**
     * 通过父id获取下级列表
     *
     * @param id ID
     * @return
     */
    List<Sys_dict> getSubListById(String id);

    /**
     * 通过树PATH获取子级
     *
     * @param path 路径
     * @return
     */
    Map<String, String> getSubMapByPath(String path);

    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    Map<String, String> getSubMapByCode(String code);

    /**
     * 通过id获取下级map
     *
     * @param id ID
     * @return
     */
    Map<String, String> getSubMapById(String id);

    /**
     * 保存数据字典
     *
     * @param dict 字典对象
     * @param pid  父ID
     */
    void save(Sys_dict dict, String pid);

    /**
     * 级联删除数据
     *
     * @param dict 字典对象
     */
    void deleteAndChild(Sys_dict dict);

    /**
     * 清空缓存
     */
    void cacheClear();
}
