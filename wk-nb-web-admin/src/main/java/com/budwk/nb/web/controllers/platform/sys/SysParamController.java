package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.sys.models.Sys_config;
import com.budwk.nb.sys.services.SysConfigService;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wizzer.cn on 2019/12/10
 */
@IocBean
@At("/api/{version}/platform/sys/param")
@Ok("json")
@ApiVersion("1.0.0")
public class SysParamController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysConfigService sysConfigService;
    @Inject
    private PubSubService pubSubService;

    /**
     * @api {post} /api/1.0.0/platform/sys/param/list 分页查询
     * @apiName list
     * @apiGroup SYS_PARAM
     * @apiPermission sys.manage.param
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
    @RequiresPermissions("sys.manage.param")
    public Object list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = sysConfigService.listPageLinks(pageNo, pageSize, cnd, "^(createdByUser|updatedByUser)$");
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/param/create 新增
     * @apiName create
     * @apiGroup SYS_PARAM
     * @apiPermission sys.manage.param.create
     * @apiVersion 1.0.0
     * @apiParam {Object} config  表单
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @RequiresPermissions("sys.manage.param.create")
    @SLog(tag = "新增参数")
    public Object create(@Param("..") Sys_config config, HttpServletRequest req) {
        try {
            if (sysConfigService.insert(config) != null) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_param");
            }
            req.setAttribute("_slog_msg", String.format("%s=>%s", config.getConfigKey(), config.getConfigValue()));
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/param/get/:id 获取参数信息
     * @apiName get
     * @apiGroup SYS_PARAM
     * @apiPermission sys.manage.param
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.param")
    public Object getData(String id) {
        try {
            Sys_config config = sysConfigService.fetch(id);
            if (config == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(config);
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/param/update 修改参数信息
     * @apiName update
     * @apiGroup SYS_PARAM
     * @apiPermission sys.manage.param.update
     * @apiVersion 1.0.0
     * @apiParam {Object} config  表单
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.param.update")
    @SLog(tag = "修改参数")
    public Object update(@Param("..") Sys_config config, HttpServletRequest req) {
        try {
            config.setUpdatedBy(StringUtil.getPlatformUid());
            if (sysConfigService.updateIgnoreNull(config) > 0) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_param");
            }
            req.setAttribute("_slog_msg", String.format("%s=>%s", config.getConfigKey(), config.getConfigValue()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/param/delete/:configKey 删除参数信息
     * @apiName delete
     * @apiGroup SYS_PARAM
     * @apiPermission sys.manage.param.delete
     * @apiVersion 1.0.0
     * @apiParam {String} configKey 参数Key
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.param.delete")
    @SLog(tag = "删除参数")
    public Object delete(String configKey, HttpServletRequest req) {
        try {
            if (Strings.sBlank(configKey).startsWith("App")) {
                return Result.error("系统参数不可删除");
            }
            if (sysConfigService.delete(configKey) > 0) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_param");
            }
            req.setAttribute("_slog_msg", String.format("key==%s", configKey));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
