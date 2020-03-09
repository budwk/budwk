package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.commons.base.service.BaseService;

/**
 * @author wizzer(wizzer.cn) on 2016/12/22.
 */
public interface SysUnitService extends BaseService<Sys_unit> {
    /**
     * 保存单位
     *
     * @param unit 单位对象
     * @param pid 父ID
     */
    void save(Sys_unit unit, String pid);

    /**
     * 级联删除单位及单位下用户
     *
     * @param unit 单位对象
     */
    void deleteAndChild(Sys_unit unit);

    /**
     * 清空缓存
     */
    void clearCache();
}
