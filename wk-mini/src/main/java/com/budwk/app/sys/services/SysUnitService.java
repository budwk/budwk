package com.budwk.app.sys.services;

import com.budwk.app.sys.enums.SysUnitType;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseService;
import org.nutz.lang.Strings;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysUnitService extends BaseService<Sys_unit> {
    /**
     * 保存单位
     *
     * @param unit 单位对象
     */
    void save(Sys_unit unit);

    /**
     * 修改单位
     *
     * @param unit      单位对象
     * @param leaders   本单位领导
     * @param highers   上级主管领导
     * @param assigners 上级分管领导
     */
    void update(Sys_unit unit, String[] leaders, String[] highers, String[] assigners);

    /**
     * 级联删除单位及单位下用户
     *
     * @param unit 单位对象
     */
    void deleteAndChild(Sys_unit unit);

    /**
     * 查询本级及下一级单位用户
     *
     * @param username 用户昵称
     * @param unitId   单位ID
     * @return
     */
    Pagination searchUser(String username, String unitId);

    /**
     * 获取单位所在分公司/总公司
     * @param unitId
     * @return
     */
    String getMasterCompanyId(String unitId);

    /**
     * 获取单位所在分公司/总公司
     * @param unitId
     * @return
     */
    String getMasterCompanyPath(String unitId);

    /**
     * 获取单位所在分公司/总公司
     *
     * @param unitId 单位ID
     * @return
     */
    Sys_unit getMasterCompany(String unitId);

    /**
     * 获取公司直属部门及子部门(不含分公司/分公司部门)
     * @param parentId
     * @return
     */
    List<String> getSubUnitIds(String parentId);
}
