package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.sys.models.Sys_lang;
import com.budwk.nb.sys.models.Sys_lang_local;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysLangService;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.commons.base.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2019/11/15
 */
@IocBean
@At("/api/{version}/platform/sys/lang")
@Ok("json")
@ApiVersion("1.0.0")
public class SysLangController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysLangService sysLangService;
    @Inject
    @Reference
    private SysLangLocalService sysLangLocalService;

    /**
     * @api {get} /api/1.0.0/platform/sys/lang/locale 查询多语言区域
     * @apiName locale
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言数据
     */
    @At("/locale")
    @Ok("json")
    @RequiresPermissions("sys.config.lang")
    public Object locale() {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.asc("location");
            return Result.success().addData(sysLangLocalService.query(cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/lang/get 查询字符串
     * @apiName get
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言数据
     */
    @At("/get")
    @Ok("json")
    @RequiresPermissions("sys.config.lang")
    public Object getLang(@Param("lang_key") String lang_key) {
        try {
            NutMap nutMap = NutMap.NEW();
            nutMap.addv("lang_key", lang_key);
            NutMap resMap = NutMap.NEW();
            List<Sys_lang> langList = sysLangService.query(Cnd.where("lang_key", "=", lang_key));
            for (Sys_lang lang : langList) {
                resMap.addv(lang.getLocale(), lang.getLang_value());
            }
            nutMap.addv("lang_value", resMap);
            return Result.success().addData(nutMap);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/lang/create_or_update 新增或修改多语言字符串
     * @apiName create_or_update
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang.create
     * @apiVersion 1.0.0
     * @apiParam {String} lang_key   多语言字符串标识
     * @apiParam {String} lang_value[*]   多语言字符串值
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言数据
     */
    @At("/create_or_update")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.create")
    public Object createOrUpdate(@Param("lang_key") String lang_key, HttpServletRequest req) {
        try {
            List<Sys_lang_local> list = sysLangLocalService.query();
            for (Sys_lang_local local : list) {
                String lang_value = Strings.sNull(req.getParameter("lang_value[" + local.getLocale() + "]"));
                int num = sysLangService.count(Cnd.where("lang_key", "=", lang_key).and("locale", "=", local.getLocale()));
                if (num > 0) {
                    sysLangService.update(Chain.make("lang_value", lang_value), Cnd.where("lang_key", "=", lang_key).and("locale", "=", local.getLocale()));
                } else {
                    Sys_lang sysLang = new Sys_lang();
                    sysLang.setLocale(local.getLocale());
                    sysLang.setLang_key(lang_key);
                    sysLang.setLang_value(lang_value);
                    sysLang.setCreatedBy(StringUtil.getPlatformUid());
                    sysLang.setUpdatedBy(StringUtil.getPlatformUid());
                    sysLangService.insert(sysLang);
                }
            }
            sysLangService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/lang/list 查询多语言字符串
     * @apiName list
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang
     * @apiVersion 1.0.0
     * @apiParam {String} locale   多语言区域
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言字符串
     */
    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.config.lang")
    public Object list(@Param("locale") String locale, @Param("lang_key") String lang_key, @Param("lang_value") String lang_value, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(locale)) {
                cnd.and("locale", "=", locale);
            }
            if (Strings.isNotBlank(lang_key)) {
                cnd.and(Cnd.likeEX("lang_key",lang_key));
            }
            if (Strings.isNotBlank(lang_value)) {
                cnd.and(Cnd.likeEX("lang_value",lang_value));
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysLangService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/lang/language_create 新增语言
     * @apiName language_create
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang.create
     * @apiVersion 1.0.0
     * @apiParam {String} name   名称
     * @apiParam {String} locale   语言标识符
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/language_create")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.create")
    public Object languageCreate(@Param("name") String name, @Param("locale") String locale, HttpServletRequest req) {
        try {
            if (sysLangLocalService.count(Cnd.where("locale", "=", locale)) > 0) {
                return Result.error("sys.lang.error.localeExist");
            }
            Sys_lang_local local = new Sys_lang_local();
            local.setLocale(locale);
            local.setName(name);
            local.setCreatedBy(StringUtil.getPlatformUid());
            local.setUpdatedBy(StringUtil.getPlatformUid());
            sysLangLocalService.insert(local);
            sysLangLocalService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/lang/language_delete 删除语言
     * @apiName language_delete
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang.delete
     * @apiVersion 1.0.0
     * @apiParam {String} locale   语言标识符
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/language_delete")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.delete")
    public Object languageDelete(@Param("locale") String locale, HttpServletRequest req) {
        try {
            sysLangLocalService.clearLocal(locale);
            sysLangService.clearCache();
            sysLangLocalService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/lang/delete 删除字符串
     * @apiName delete
     * @apiGroup SYS_LANG
     * @apiPermission sys.config.lang.delete
     * @apiVersion 1.0.0
     * @apiParam {String} lang_key   字符串标识
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete")
    @POST
    @Ok("json")
    @RequiresPermissions("sys.config.lang.delete")
    public Object deleteLang(@Param("lang_key") String lang_key, HttpServletRequest req) {
        try {
            sysLangService.clear(Cnd.where("lang_key", "=", lang_key));
            sysLangService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
