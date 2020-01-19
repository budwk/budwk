package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.sys.services.SysApiService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/9
 */
@IocBean
@At("/api/{version}/platform/sys/api")
@Ok("json")
@ApiVersion("1.0.0")
public class SysApiController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysApiService sysApiService;

    /**
     * @api {post} /api/1.0.0/platform/sys/api/list 分页查询
     * @apiName list
     * @apiGroup SYS_API
     * @apiPermission sys.config.api
     * @apiVersion 1.0.0
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  分页数据
     */
    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.config.api")
    public Object list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = sysApiService.listPage(pageNo, pageSize, cnd);
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/api/create 新建密钥
     * @apiName create
     * @apiGroup SYS_API
     * @apiPermission sys.config.api.create
     * @apiVersion 1.0.0
     * @apiParam {String} name  应用名称
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @RequiresPermissions("sys.config.api.create")
    @SLog(tag = "新建密钥", msg = "应用名称:${name}")
    public Object create(@Param("name") String name, HttpServletRequest req) {
        try {
            sysApiService.createAppkey(name, StringUtil.getPlatformUid());
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/api/delete 新建密钥
     * @apiName delete
     * @apiGroup SYS_API
     * @apiPermission sys.config.api.delete
     * @apiVersion 1.0.0
     * @apiParam {String} appid  appid
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @RequiresPermissions("sys.config.api.delete")
    @SLog(tag = "删除密钥", msg = "Appid:${appid}")
    public Object delete(String appid, HttpServletRequest req) {
        try {
            sysApiService.deleteAppkey(appid);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/api/disabled 启用禁用
     * @apiName disabled
     * @apiGroup SYS_API
     * @apiPermission sys.config.api.update
     * @apiVersion 1.0.0
     * @apiParam {String} appid   appid
     * @apiParam {String} disabled   true/false
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.api.update")
    @SLog(tag = "启用禁用密钥")
    public Object changeDisabled(@Param("appid") String appid, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            sysApiService.updateAppkey(appid, disabled, StringUtil.getPlatformUid());
            if (disabled) {
                req.setAttribute("_slog_msg", "appid:" + appid + " " + Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", "appid:" + appid + " " + Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            sysApiService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
