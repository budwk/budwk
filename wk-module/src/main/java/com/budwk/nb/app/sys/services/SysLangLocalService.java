package com.budwk.nb.app.sys.services;

import com.budwk.nb.app.sys.models.Sys_lang_local;
import com.budwk.nb.common.base.service.BaseService;

import java.util.List;

public interface SysLangLocalService extends BaseService<Sys_lang_local> {
    /**
     * 获取多语言区域
     *
     * @return
     */
    List<Sys_lang_local> getLocal();

    void clearLocal(String locale);
    /**
     * 获取多语言区域Json
     *
     * @return
     */
    String getLocalJson();

    void clearCache();
}
