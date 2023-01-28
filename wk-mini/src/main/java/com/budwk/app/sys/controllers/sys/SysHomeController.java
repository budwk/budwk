package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import com.budwk.app.sys.enums.SysMsgScope;
import com.budwk.app.sys.enums.SysMsgType;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.SysConfigService;
import com.budwk.app.sys.services.SysMsgService;
import com.budwk.app.sys.services.SysMsgUserService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.common.utils.PwdUtil;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.log.provider.ISysLogProvider;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/home")
@SLog(tag = "用户中心")
@ApiDefinition(tag = "用户中心")
@Slf4j
public class SysHomeController {
    @Inject
    private SysMsgService sysMsgService;
    @Inject
    private SysMsgUserService sysMsgUserService;
    @Inject
    private SysConfigService sysConfigService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private ISysLogProvider sysLogProvider;

    @At("/msg/wsmsg")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取websocket消息")
    @SaCheckLogin
    public Result<?> wsmsg() {
        sysMsgService.getMsg(SecurityUtil.getUserId(), true);
        return Result.success();
    }

    @At("/msg/data")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取消息配置数据")
    @SaCheckLogin
    public Result<?> data() {
        NutMap map = NutMap.NEW();
        map.addv("types", SysMsgType.values());
        map.addv("scopes", SysMsgScope.values());
        return Result.data(map);
    }

    @At("/msg/my_msg_list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "查询消息列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "status", example = "", description = "读取状态"),
                    @ApiFormParam(name = "type", example = "", description = "消息类型"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    public Result<?> my_msg_list(@Param("status") String status, @Param("type") String type, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {

        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(status) && "read".equals(status)) {
            cnd.and("a.status", "=", 1);
        }
        if (Strings.isNotBlank(status) && "unread".equals(status)) {
            cnd.and("a.status", "=", 0);
        }
        cnd.and("a.userId", "=", SecurityUtil.getUserId());
        cnd.and("a.delFlag", "=", false);
        cnd.desc("a.createdAt");
        if (Strings.isNotBlank(type) && !"all".equals(type)) {
            cnd.and("b.type", "=", type);
        }
        Sql sql = Sqls.create("SELECT b.type,b.title,b.sendat,a.* FROM sys_msg b LEFT JOIN sys_msg_user a ON b.id=a.msgid $condition");
        sql.setCondition(cnd);
        Sql sqlCount = Sqls.create("SELECT count(*) FROM sys_msg b LEFT JOIN sys_msg_user a ON b.id=a.msgid $condition");
        sqlCount.setCondition(cnd);
        return Result.data(sysMsgService.listPage(pageNo, pageSize, sql, sqlCount));

    }

    @At("/msg/status/read_more")
    @Ok("json")
    @POST
    @SaCheckPermission("home.msg.read")
    @ApiOperation(description = "消息批量设置已读")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", description = "ID数组")
            }
    )
    @ApiResponses
    public Result<?> read_more(@Param("ids") String[] ids, HttpServletRequest req) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        sysMsgUserService.update(org.nutz.dao.Chain.make("status", 1).add("readAt", Times.now().getTime())
                .add("updatedAt", Times.now().getTime()).add("updatedBy", SecurityUtil.getUserId()), Cnd.where("id", "in", ids).and("userId", "=", SecurityUtil.getUserId()).and("status", "=", 0));
        sysMsgService.getMsg(SecurityUtil.getUserId(), false);
        return Result.success();

    }

    @At("/msg/status/read_all")
    @Ok("json")
    @POST
    @SaCheckPermission("home.msg.read")
    @ApiOperation(description = "消息全部设置已读")
    @ApiFormParams
    @ApiResponses
    public Result<?> readAll(HttpServletRequest req) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        sysMsgUserService.update(org.nutz.dao.Chain.make("status", 1).add("readAt", Times.now().getTime())
                .add("updatedAt", Times.now().getTime()).add("updatedBy", SecurityUtil.getUserId()), Cnd.where("userId", "=", SecurityUtil.getUserId()).and("status", "=", 0));
        sysMsgService.getMsg(SecurityUtil.getUserId(), false);
        return Result.success();
    }


    @At("/msg/get/{id}")
    @Ok("json")
    @POST
    @SaCheckLogin
    @ApiOperation(description = "获取一条消息内容")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "消息ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        // 判断是否有权限查看
        int num = sysMsgUserService.count(Cnd.where("msgid", "=", id).and("userId", "=", SecurityUtil.getUserId()));
        if (num > 0) {
            return Result.data(sysMsgService.fetch(id));
        }
        return Result.error(ResultCode.USER_NOT_PERMISSION);

    }


    @At("/msg/status/read_one/{id}")
    @Ok("json")
    @POST
    @SaCheckPermission("home.msg.read")
    @ApiOperation(description = "设置一条消息已读")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "消息ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> read_one(String id, HttpServletRequest req) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        sysMsgUserService.update(org.nutz.dao.Chain.make("status", 1).add("readAt", Times.now().getTime())
                .add("updatedAt", Times.now().getTime()).add("updatedBy", SecurityUtil.getUserId()), Cnd.where("msgid", "=", id).and("userId", "=", SecurityUtil.getUserId()).and("status", "=", 0));
        sysMsgService.getMsg(SecurityUtil.getUserId(), false);
        return Result.success();
    }

    @At("/user/avatar")
    @Ok("json")
    @POST
    @SaCheckPermission("home.user.update")
    @ApiOperation(description = "设置用户头像")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "avatar", description = "头像路径", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> setUserAvatar(@Param("avatar") String avatar) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        sysUserService.update(Chain.make("avatar", avatar), Cnd.where("id", "=", SecurityUtil.getUserId()));
        sysUserService.cacheRemove(SecurityUtil.getUserId());
        return Result.success();
    }

    @At("/user/pwd")
    @Ok("json")
    @POST
    @SaCheckPermission("home.user.resetPwd")
    @ApiOperation(description = "修改用户密码")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "oldPassword", example = "", description = "原密码", required = true),
                    @ApiFormParam(name = "newPassword", example = "", description = "新密码", required = true, check = true, validation = Validation.PASSWORD)
            }
    )
    @ApiResponses
    public Result<?> setUserPassword(@Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        Sys_user user = sysUserService.fetch(SecurityUtil.getUserId());
        if (user == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        if (!user.getPassword().equals(PwdUtil.getPassword(oldPassword, user.getSalt()))) {
            return Result.error("原密码不正确");
        }
        // 重置密码内部已清除缓存
        sysUserService.resetPwd(user.getId(), newPassword, false);
        return Result.success();
    }

    @At("/user/info")
    @Ok("json")
    @POST
    @SaCheckPermission("home.user.update")
    @ApiOperation(description = "设置用户资料")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "username", description = "姓名", required = true),
                    @ApiFormParam(name = "sex", description = "性别"),
                    @ApiFormParam(name = "email", description = "EMail", check = true, validation = Validation.EMAIL),
                    @ApiFormParam(name = "mobile", description = "手机号码", check = true, validation = Validation.MOBILE)
            }
    )
    @ApiResponses
    public Result<?> setUserInfo(@Param("username") String username, @Param("email") String email, @Param("mobile") String mobile) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && sysConfigService.getBoolean(SecurityUtil.getAppId(), "AppDemoEnv")) {
            return Result.error(ResultCode.DEMO_ERROR);
        }
        if (Strings.isNotBlank(email)) {
            if (sysUserService.count(Cnd.where("email", "=", email).and("id", "<>", SecurityUtil.getUserId())) > 0) {
                return Result.error("邮箱地址已存在");
            }
        }
        if (Strings.isNotBlank(mobile)) {
            if (sysUserService.count(Cnd.where("mobile", "=", mobile).and("id", "<>", SecurityUtil.getUserId())) > 0) {
                return Result.error("手机号码已存在");
            }
        }
        sysUserService.update(Chain.make("username", username)
                        .add("email", email)
                        .add("mobile", mobile)
                , Cnd.where("id", "=", SecurityUtil.getUserId()));
        sysUserService.cacheRemove(SecurityUtil.getUserId());
        return Result.success();
    }

    @At("/user/get")
    @Ok("json:{locked:'^(password|salt)$',ignoreNull:false}")
    @ApiOperation(description = "获取用户信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", example = "", description = "用户ID", required = true)
            }
    )
    @ApiResponses
    @SaCheckLogin
    public Result<?> getUserInfo() {
        Sys_user user = sysUserService.fetch(SecurityUtil.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }
        sysUserService.fetchLinks(user, "^(unit|post|roles)$");
        return Result.success().addData(user);
    }

    @At("/user/log")
    @Ok("json:{locked:'^(ip|params|result|browser|)$',ignoreNull:false}")
    @ApiOperation(description = "用户操作日志(最近2个月)")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckLogin
    public Result<?> getUserLog(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.success().addData(sysLogProvider.list(
                null, null, null, null, null, SecurityUtil.getUserId(), null, null,
                DateUtil.lastMonth().getTime(), 0, pageOrderName, pageOrderBy, pageNo, pageSize
        ));
    }
}
