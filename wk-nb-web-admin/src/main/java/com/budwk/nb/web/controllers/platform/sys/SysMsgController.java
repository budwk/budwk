package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.sys.enums.SysMsgTypeEnum;
import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.sys.services.SysMsgService;
import com.budwk.nb.sys.services.SysMsgUserService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.web.commons.ext.websocket.WkNotifyService;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer.cn on 2019/12/18
 */
@IocBean
@At("/api/{version}/platform/sys/msg")
@Ok("json")
@ApiVersion("1.0.0")
public class SysMsgController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysMsgService sysMsgService;
    @Inject
    @Reference
    private SysMsgUserService sysMsgUserService;
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    private WkNotifyService wkNotifyService;
    @Inject
    private ShiroUtil shiroUtil;

    /**
     * @api {get} /api/1.0.0/platform/sys/msg/get_type 获取消息类型
     * @apiName get_type
     * @apiGroup SYS_MSG
     * @apiPermission sys.manage.msg
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言数据
     */
    @At("/get_type")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.msg")
    public Object getType() {
        try {
            return Result.success().addData(SysMsgTypeEnum.values());
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/msg/list 查询消息列表
     * @apiName list
     * @apiGroup SYS_MSG
     * @apiPermission sys.manage.msg
     * @apiVersion 1.0.0
     * @apiParam {String} type    消息类型
     * @apiParam {String} title   消息标题
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
    @RequiresPermissions("sys.manage.msg")
    public Object list(@Param("type") String type, @Param("title") String title, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(type)) {
                cnd.and("type", "=", type);
            }
            if (Strings.isNotBlank(title)) {
                cnd.and(Cnd.likeEX("title", title));
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            List<Map> mapList = new ArrayList<>();
            Pagination pagination = sysMsgService.listPage(pageNo, pageSize, cnd);
            for (Object msg : pagination.getList()) {
                NutMap map = Lang.obj2nutmap(msg);
                map.put("all_num", sysMsgUserService.count(Cnd.where("msgId", "=", map.get("id", ""))));
                map.put("unread_num", sysMsgUserService.count(Cnd.where("msgId", "=", map.get("id", "")).and("status", "=", 0)));
                mapList.add(map);
            }
            pagination.setList(mapList);
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/msg/get_user_view_list 消息发送对象列表
     * @apiName get_user_view_list
     * @apiGroup SYS_MSG
     * @apiPermission sys.manage.msg
     * @apiVersion 1.0.0
     * @apiParam {String} type    消息类型
     * @apiParam {String} id      消息ID
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  分页数据
     */
    @At("/get_user_view_list")
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.msg")
    public Object getUserViewList(@Param("type") String type, @Param("id") String id, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            String sql = "SELECT a.loginname,a.username,a.mobile,a.email,a.disabled,a.unitid,b.name as unitname,c.status,c.readat FROM sys_user a,sys_unit b,sys_msg_user c WHERE a.unitid=b.id \n" +
                    "and a.loginname=c.loginname and c.msgId='" + id + "' ";
            if (Strings.isNotBlank(type) && "unread".equals(type)) {
                sql += " and c.status=0 ";
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                sql += " order by a." + pageOrderName + " " + PageUtil.getOrder(pageOrderBy);
            }
            return Result.success().addData(sysMsgService.listPage(pageNo, pageSize, Sqls.create(sql)));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/msg/delete/:id 删除消息
     * @apiName delete
     * @apiGroup SYS_MSG
     * @apiPermission sys.manage.msg
     * @apiVersion 1.0.0
     * @apiParam {String} id      消息ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言字符串
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.msg.delete")
    @SLog(tag = "站内消息")
    public Object delete(String id, HttpServletRequest req) {
        try {
            Sys_msg msg = sysMsgService.fetch(id);
            if (msg == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            req.setAttribute("_slog_msg", msg.getTitle());
            sysMsgService.deleteMsg(id);
            sysMsgUserService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/msg/select_user_list 查询用户列表
     * @apiName select_user_list
     * @apiGroup SYS_MSG
     * @apiPermission sys.manage.msg
     * @apiVersion 1.0.0
     * @apiParam {String} searchName    查询字段
     * @apiParam {String} searchKeyword  查询内容
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  分页数据
     */
    @At("/select_user_list")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("sys.manage.msg")
    public Object selectUserList(@Param("searchName") String searchName, @Param("searchKeyword") String searchKeyword, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!shiroUtil.hasRole("sysadmin")) {
                cnd.and("unitid", "=", StringUtil.getPlatformUserUnitid());
            }
            if (Strings.isNotBlank(searchName) && Strings.isNotBlank(searchKeyword)) {
                cnd.and(searchName, "like", "%" + searchKeyword + "%");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysUserService.listPageLinks(pageNo, pageSize, cnd, "unit"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/msg/create 发送消息
     * @apiName create
     * @apiGroup SYS_MSG
     * @apiPermission sys.manage.msg
     * @apiVersion 1.0.0
     * @apiParam {Object} nutMap 表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  分页数据
     */
    @At("/create")
    @Ok("json")
    @RequiresPermissions("sys.manage.msg.create")
    @SLog(tag = "发送消息", msg = "${msg.title}")
    public Object create(@Param("..") Sys_msg msg, @Param("users") String[] users, HttpServletRequest req) {
        try {
            msg.setSendAt(Times.now().getTime());
            msg.setCreatedBy(StringUtil.getPlatformUid());
            msg.setUpdatedBy(StringUtil.getPlatformUid());
            Sys_msg sys_msg = sysMsgService.saveMsg(msg, users);
            if (sys_msg != null) {
                wkNotifyService.notify(sys_msg, users);
            }
            sysMsgUserService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
