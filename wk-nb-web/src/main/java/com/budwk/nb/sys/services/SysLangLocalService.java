package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_lang_local;
import com.budwk.nb.commons.base.service.BaseService;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2019/3/16.
 */
public interface SysLangLocalService extends BaseService<Sys_lang_local> {
    /**
     * 获取多语言区域
     *
     * @return
     */
    List<Sys_lang_local> getLocal();

    /**
     * 清空语言字符串
     * @param locale 语言
     */
    void clearLocal(String locale);
    /**
     * 获取多语言区域Json
     *
     * @return
     */
    String getLocalJson();

    /**
     * 清空缓存
     */
    void clearCache();
}
