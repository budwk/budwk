package com.budwk.nb.app.sys.services;

import com.budwk.nb.app.sys.models.Sys_lang;
import com.budwk.nb.common.base.service.BaseService;
import org.nutz.mvc.impl.NutMessageMap;

public interface SysLangService extends BaseService<Sys_lang> {
    /**
     * 获取多语言字符串
     *
     * @param locale 语言标识
     * @return
     */
    NutMessageMap getLang(String locale);

    /**
     * 清空缓存
     */
    void clearCache();
}
