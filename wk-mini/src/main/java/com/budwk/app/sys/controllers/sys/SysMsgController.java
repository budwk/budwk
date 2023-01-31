package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.enums.SysMsgScope;
import com.budwk.app.sys.enums.SysMsgType;
import com.budwk.app.sys.models.Sys_msg;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.*;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/msg")
@SLog(tag = "消息管理")
@ApiDefinition(tag = "消息管理")
@Slf4j
public class SysMsgController {
    @Inject
    private SysMsgService sysMsgService;
    @Inject
    private SysAppService sysAppService;
    @Inject
    private SysMsgUserService sysMsgUserService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysUserService sysUserService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取配置数据")
    @SaCheckLogin
    public Result<?> data() {
        NutMap map = NutMap.NEW();
        map.addv("apps", sysAppService.listAll());
        map.addv("types", SysMsgType.values());
        map.addv("scopes", SysMsgScope.values());
        return Result.data(map);
    }

    @At("/list")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @POST
    @ApiOperation(name = "获取信息列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "appId", example = "", description = "应用ID"),
                    @ApiFormParam(name = "type", example = "", description = "消息类型"),
                    @ApiFormParam(name = "title", example = "", description = "消息标题"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.msg")
    public Result<?> list(@Param("appId") String appId, @Param("type") String type, @Param("title") String title, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(appId)) {
            cnd.and("appId", "=", appId);
        }
        if (Strings.isNotBlank(type)) {
            cnd.and("type", "=", type);
        }
        if (Strings.isNotBlank(title)) {
            cnd.and(Cnd.likeEX("title", title));
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        List<NutMap> mapList = new ArrayList<>();
        Pagination pagination = sysMsgService.listPageLinks(pageNo, pageSize, cnd, "^(createdByUser)$");
        for (Sys_msg msg : pagination.getList(Sys_msg.class)) {
            NutMap map = Lang.obj2nutmap(msg);
            map.put("all_num", sysMsgUserService.count(Cnd.where("msgId", "=", msg.getId())));
            map.put("unread_num", sysMsgUserService.count(Cnd.where("msgId", "=", msg.getId()).and("status", "=", 0)));
            mapList.add(map);
        }
        pagination.setList(mapList);
        return Result.data(pagination);
    }

    @At("/get_user_view_list")
    @Ok("json:full")
    @POST
    @ApiOperation(name = "获取发送用户列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "type", example = "all-全部消息/unread-未读消息", description = "消息类型", required = true, check = true),
                    @ApiFormParam(name = "id", example = "", description = "消息ID", required = true, check = true),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.msg")
    public Result<?> getUserViewList(@Param("type") String type, @Param("id") String id, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        String sql = "SELECT a.loginname,a.username,a.mobile,a.email,a.disabled,a.unitid,b.name as unitname,c.status,c.readat FROM sys_user a,sys_unit b,sys_msg_user c WHERE a.unitid=b.id " +
                " and a.loginname=c.loginname and c.msgId='" + id + "' ";
        if ("unread".equals(type)) {
            sql += " and c.status=0 ";
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            sql += " order by a." + pageOrderName + " " + PageUtil.getOrder(pageOrderBy);
        }
        return Result.data(sysMsgService.listPage(pageNo, pageSize, Sqls.create(sql)));
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SLog(value = "撤回消息:")
    @SaCheckPermission("sys.manage.msg.delete")
    @ApiOperation(name = "撤回消息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "消息ID")
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_msg msg = sysMsgService.fetch(id);
        if (msg == null) {
            req.setAttribute("_slog_msg", ResultCode.NULL_DATA_ERROR.getMsg());
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        req.setAttribute("_slog_msg", msg.getTitle());
        sysMsgService.deleteMsg(id);
        return Result.success();
    }

    @At("/select_user_list")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckPermission("sys.manage.msg")
    @ApiOperation(name = "获取选择用户列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "searchName", example = "", description = "查询字段"),
                    @ApiFormParam(name = "searchKeyword", example = "", description = "查询关键词"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    public Result<?> selectUserList(@Param("searchName") String searchName, @Param("searchKeyword") String searchKeyword, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (!StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            cnd.and("unitPath", "like", sysUnitService.getMasterCompanyPath(SecurityUtil.getUnitId()) + "%");
        }
        if (Strings.isNotBlank(searchName) && Strings.isNotBlank(searchKeyword)) {
            cnd.and(searchName, "like", "%" + searchKeyword + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.success().addData(sysUserService.listPageLinks(pageNo, pageSize, cnd, "unit"));
    }

    @At("/create")
    @Ok("json")
    @SaCheckPermission("sys.manage.msg.create")
    @SLog(value = "发送消息:")
    @ApiOperation(name = "发送消息")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "users", description = "指定用户ID数组")
            },
            implementation = Sys_msg.class
    )
    public Result<?> create(@Param("..") Sys_msg msg, @Param("users") String[] userId, HttpServletRequest req) {
        msg.setSendAt(System.currentTimeMillis());
        msg.setCreatedBy(SecurityUtil.getUserId());
        sysMsgService.saveMsg(msg, userId);
        req.setAttribute("_slog_msg", msg.getTitle());
        return Result.success();
    }
}
