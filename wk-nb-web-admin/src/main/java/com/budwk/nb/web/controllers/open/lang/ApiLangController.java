package com.budwk.nb.web.controllers.open.lang;

import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysLangService;
import com.budwk.nb.commons.base.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.ApiVersion;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

/**
 * 获取多语言标识符
 * @author wizzer(wizzer@qq.com) on 2019/11/13
 */
@IocBean
@At("/api/{version}/open/language")
@ApiVersion("1.0.0")
public class ApiLangController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysLangService sysLangService;
    @Inject
    @Reference
    private SysLangLocalService sysLangLocalService;

    /**
     * @api {post} /api/1.0.0/open/language/get_data 获取多语言字符串
     * @apiName get_data
     * @apiGroup OPEN_LANG
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data   map对象
     */
    @At("/get_data")
    @Ok("json")
    public Object getData(@Param("language") String language) {
        try {
            return Result.success().addData(sysLangService.getLang(language));
        } catch (Exception e) {
            log.error(e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/open/language/get_lang 获取多语言列表
     * @apiName get_lang
     * @apiGroup OPEN_LANG
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data   list对象
     */
    @At("/get_lang")
    @Ok("json")
    public Object getLang() {
        try {
            return Result.success().addData(sysLangLocalService.getLocal());
        } catch (Exception e) {
            log.error(e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/open/language/set_lang 设置当前语言
     * @apiName set_lang
     * @apiGroup OPEN_LANG
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data   list对象
     */
    @At("/set_lang")
    @Ok("json")
    public Object setLang(@Param("lang") String lang) {
        try {
            if (Strings.isNotBlank(lang)) {
                Mvcs.setLocalizationKey(lang);
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
        }
        return Result.error();
    }
}
