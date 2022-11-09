package com.budwk.app.sys.providers;


import com.budwk.app.sys.models.Sys_unit;

/**
 * @author wizzer@qq.com
 */
public interface ISysUnitProvider {
    /**
     * 获取单位所在分公司/总公司
     *
     * @param unitId 单位ID
     * @return
     */
    String getMasterCompanyId(String unitId);

    /**
     * 获取单位所在分公司/总公司
     *
     * @param unitId 单位ID
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
}
