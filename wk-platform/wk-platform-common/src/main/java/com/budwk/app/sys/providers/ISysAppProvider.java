package com.budwk.app.sys.providers;

import com.budwk.app.sys.models.Sys_app;
import com.budwk.starter.common.result.Result;

import java.util.List;

/**
 * 系统应用
 * @author wizzer@qq.com
 */
public interface ISysAppProvider {

    /**
     * 获取所有应用列表
     * @return
     */
    List<Sys_app> listAll();

    /**
     * 获取启用的应用列表
     * @return
     */
    List<Sys_app> listEnable();
}
