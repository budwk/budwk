package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_lang_local;
import com.budwk.nb.commons.base.service.BaseService;

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
