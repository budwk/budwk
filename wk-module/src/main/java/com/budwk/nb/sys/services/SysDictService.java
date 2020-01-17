package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_dict;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author wizzer(wizzer@qq.com) on 2019/3/16.
 */
public interface SysDictService extends BaseService<Sys_dict> {
    /**
     * 通过code获取名称
     *
     * @param code 字典标识
     * @return
     */
    String getNameByCode(String code);

    /**
     * 通过ID获取名称
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
     * 通过ID获取子级
     *
     * @param id ID
     * @return
     */
    List<Sys_dict> getSubListById(String id);

    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    List<Sys_dict> getSubListByCode(String code);

    /**
     * 通过树PATH获取子级
     *
     * @param path 路径
     * @return
     */
    Map getSubMapByPath(String path);

    /**
     * 通过ID获取子级
     *
     * @param id ID
     * @return
     */
    Map getSubMapById(String id);

    /**
     * 通过code获取子级
     *
     * @param code 标识
     * @return
     */
    Map getSubMapByCode(String code);

    /**
     * 保存数据字典
     *
     * @param dict 字典对象
     * @param pid 父ID
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
    void clearCache();
}
