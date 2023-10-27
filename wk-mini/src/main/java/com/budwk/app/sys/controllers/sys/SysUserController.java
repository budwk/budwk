package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.models.Sys_role;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.SysGroupService;
import com.budwk.app.sys.services.SysPostService;
import com.budwk.app.sys.services.SysUnitService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.excel.utils.ExcelUtil;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/user")
@SLog(tag = "用户管理")
@ApiDefinition(tag = "用户管理")
@Slf4j
public class SysUserController {
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysPostService sysPostService;
    @Inject
    private SysGroupService sysGroupService;

    @At("/unitlist")
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue3树形列表查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "name", example = "", description = "单位名称")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.user")
    public Result<?> getUnitList(
            @Param("name") String name) {
        Cnd cnd = Cnd.NEW();
        // 非管理员显示所属单位及下级单位
        if (!StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Sys_unit unit = sysUnitService.fetch(SecurityUtil.getUnitId());
            cnd.and("path", "like", unit.getPath() + "%");
        }
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(sysUnitService.query(cnd));
    }

    @At("/unit")
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue2获取单位树数据")
    @ApiImplicitParams
    @ApiResponses
    @SaCheckPermission("sys.manage.user")
    public Result<?> getUnitTree() {
        String pid = "";
        List<Sys_unit> list = new ArrayList<>();
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Cnd cnd = Cnd.NEW();
            cnd.asc("location").asc("path");
            list = sysUnitService.query(cnd);
        } else {
            //如果非超级管理员,查找最顶级单位
            Sys_unit unit = sysUnitService.fetch(sysUnitService.getMasterCompanyId(SecurityUtil.getUnitId()));
            pid = unit.getParentId();
            list = sysUnitService.query(Cnd.where("path", "like", unit.getPath() + "%").asc("location").asc("path"));
        }
        NutMap unitMap = NutMap.NEW();
        for (Sys_unit unit : list) {
            List<Sys_unit> list1 = unitMap.getList(unit.getParentId(), Sys_unit.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(unit);
            unitMap.put(unit.getParentId(), list1);
        }
        return Result.data(getTree(unitMap, pid));
    }

    private List<NutMap> getTree(NutMap unitMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Sys_unit> subList = unitMap.getList(pid, Sys_unit.class);
        for (Sys_unit unit : subList) {
            NutMap map = Lang.obj2nutmap(unit);
            map.put("label", unit.getName());
            if (unit.isHasChildren() || (unitMap.get(unit.getId()) != null)) {
                map.put("children", getTree(unitMap, unit.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    @At("/post")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取职务列表")
    @ApiImplicitParams
    @ApiResponses
    @SaCheckPermission("sys.manage.user")
    public Result<?> post(HttpServletRequest req) {
        return Result.data(sysPostService.query());
    }

    @At("/count/{unitPath}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取职务列表")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "unitPath", description = "单位PATH", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.user")
    public Result<?> count(@Param("unitPath") String unitPath, HttpServletRequest req) {
        int allNumber = sysUserService.count(Cnd.where("unitPath", "like", unitPath + "%"));
        int enabledNumber = sysUserService.count(Cnd.where("unitPath", "like", unitPath + "%").and("disabled", "=", false));
        int disabledNumber = sysUserService.count(Cnd.where("unitPath", "like", unitPath + "%").and("disabled", "=", true));
        return Result.data(NutMap.NEW().addv("allNumber", allNumber).addv("enabledNumber", enabledNumber).addv("disabledNumber", disabledNumber));
    }

    @At("/list")
    @Ok("json:{locked:'^(password|salt)$',ignoreNull:false}")
    @POST
    @ApiOperation(name = "获取用户列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "unitPath", example = "", description = "单位PATH"),
                    @ApiFormParam(name = "postId", example = "", description = "职务ID"),
                    @ApiFormParam(name = "username", example = "", description = "用户姓名"),
                    @ApiFormParam(name = "loginname", example = "", description = "用户名"),
                    @ApiFormParam(name = "mobile", example = "", description = "手机号码"),
                    @ApiFormParam(name = "disabled", example = "", description = "用户状态", type = "boolean"),
                    @ApiFormParam(name = "query", example = "", description = "查询关键词"),
                    @ApiFormParam(name = "beginTime", example = "", description = "开始时间", type = "long"),
                    @ApiFormParam(name = "endTime", example = "", description = "结束时间", type = "long"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.user")
    public Result<?> list(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("disabled") Boolean disabled, @Param("mobile") String mobile, @Param("unitPath") String unitPath, @Param("postId") String postId, @Param("username") String username, @Param("loginname") String loginname, @Param("query") String query, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(unitPath)) {
            cnd.and("unitPath", "like", unitPath + "%");
        }
        if (Strings.isNotBlank(postId)) {
            cnd.and("postId", "=", postId);
        }
        if (Strings.isNotBlank(username)) {
            cnd.and("username", "like", "%" + username + "%");
        }
        if (Strings.isNotBlank(loginname)) {
            cnd.and("loginname", "like", "%" + loginname + "%");
        }
        if (Strings.isNotBlank(mobile)) {
            cnd.and("mobile", "like", "%" + mobile + "%");
        }
        if (beginTime != null && endTime != null) {
            cnd.and("createdAt", ">=", beginTime);
            cnd.and("createdAt", "<=", endTime);
        }
        if (disabled != null) {
            cnd.and("disabled", "=", disabled);
        }
        if (Strings.isNotBlank(query)) {
            cnd.and(Cnd.exps("loginname", "like", "%" + query + "%").or("username", "like", "%" + query + "%")
                    .or("mobile", "like", "%" + query + "%"));
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(sysUserService.listPageLinks(pageNo, pageSize, cnd, "^(unit|roles|createdByUser|updatedByUser)$"));
    }

    @At("/number")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取员工编号")
    @ApiImplicitParams
    @ApiResponses
    @SaCheckPermission("sys.manage.user")
    public Result<?> number(HttpServletRequest req) {
        Sql sql = Sqls.create("select max(CAST(serialNo AS SIGNED)) from sys_user");
        sql.setCallback(Sqls.callback.integer());
        sysUserService.dao().execute(sql);
        return Result.data(sql.getInt() + 1);
    }

    @At("/group")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.user")
    @ApiOperation(name = "获取公司(或为上级单位)角色组及角色")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "unitId", description = "单位ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> group(@Param("unitId") String unitId, HttpServletRequest req) {
        if (StpUtil.hasPermission("sys.manage.role.system")) {
            //有系统公用权限可分配公用角色
            return Result.data(sysGroupService.query(Cnd.where("unitId", "=",
                            sysUnitService.getMasterCompanyId(unitId)).or("unitid", "=", "").asc("createdAt"), "roles",
                    // 排除public角色,公共角色无需分配即可拥有
                    Cnd.where("code", "<>", "public")));
        } else {
            return Result.data(sysGroupService.query(Cnd.where("unitId", "=",
                            sysUnitService.getMasterCompanyId(unitId)).asc("createdAt"), "roles",
                    // 排除public角色,公共角色无需分配即可拥有
                    Cnd.where("code", "<>", "public")));
        }
    }

    @At("/create")
    @Ok("json")
    @POST
    @SLog(value = "新增用户,用户名:${user.loginname}")
    @ApiOperation(name = "新增用户")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "roleIds", description = "角色ID数组")
            },
            implementation = Sys_user.class
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.user.create")
    public Result<?> create(@Param("..") Sys_user user, @Param("roleIds") String[] roleIds) {
        int checkNumber = sysUserService.count(Cnd.where("serialNo", "=", user.getSerialNo()));
        if (checkNumber > 0) {
            return Result.error("用户编号已存在");
        }
        if (Strings.isNotBlank(user.getMobile())) {
            checkNumber = sysUserService.count(Cnd.where("mobile", "=", user.getMobile()));
            if (checkNumber > 0) {
                return Result.error("手机号已存在");
            }
        }
        checkNumber = sysUserService.count(Cnd.where("loginname", "=", Strings.trim(user.getLoginname())));
        if (checkNumber > 0) {
            return Result.error("用户名已存在");
        }
        if (Strings.isNotBlank(user.getEmail())) {
            checkNumber = sysUserService.count(Cnd.where("email", "=", Strings.trim(user.getEmail())));
            if (Strings.isNotBlank(Strings.trim(user.getEmail())) && checkNumber > 0) {
                return Result.error("邮箱已存在");
            }
        }
        user.setCreatedBy(SecurityUtil.getUserId());
        sysUserService.create(user, roleIds);
        return Result.success();
    }

    @At("/update")
    @Ok("json")
    @POST
    @SLog(value = "修改用户,用户名:${user.loginname}")
    @ApiOperation(name = "修改用户")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "roleIds", description = "角色ID数组")
            },
            implementation = Sys_user.class
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.user.update")
    public Result<?> update(@Param("..") Sys_user user, @Param("roleIds") String[] roleIds) {
        int checkNumber = sysUserService.count(Cnd.where("serialNo", "=", user.getSerialNo()).and("id", "<>", user.getId()));
        if (checkNumber > 0) {
            return Result.error("用户编号已存在");
        }
        if (Strings.isNotBlank(user.getMobile())) {
            checkNumber = sysUserService.count(Cnd.where("mobile", "=", user.getMobile()).and("id", "<>", user.getId()));
            if (checkNumber > 0) {
                return Result.error("手机号已存在");
            }
        }
        if (Strings.isNotBlank(user.getLoginname())) {
            checkNumber = sysUserService.count(Cnd.where("loginname", "=", Strings.trim(user.getLoginname())).and("id", "<>", user.getId()));
            if (checkNumber > 0) {
                return Result.error("用户名已存在");
            }
        }
        if (Strings.isNotBlank(user.getEmail())) {
            checkNumber = sysUserService.count(Cnd.where("email", "=", Strings.trim(user.getEmail())).and("id", "<>", user.getId()));
            if (Strings.isNotBlank(Strings.trim(user.getEmail())) && checkNumber > 0) {
                return Result.error("邮箱已存在");
            }
        }
        if (user.isDisabled() && GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(Strings.trim(user.getLoginname()))) {
            return Result.error("超级管理员不可禁用");
        }
        user.setUpdatedBy(SecurityUtil.getUserId());
        sysUserService.update(user, roleIds);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json:{locked:'^(password|salt)$',ignoreNull:false}")
    @GET
    @SaCheckPermission("sys.manage.user")
    @ApiOperation(name = "获取用户信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "用户ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_user user = sysUserService.fetch(id);
        if (user == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        user = sysUserService.fetchLinks(user, "^(unit|roles)$");
        List<Sys_role> roles = user.getRoles();
        List<String> roleIds = new ArrayList<>();
        for (Sys_role role : roles) {
            roleIds.add(role.getId());
        }
        return Result.data(NutMap.NEW().addv("user", user).addv("roleIds", roleIds));
    }

    @At("/reset_pwd/{id}")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.user.update")
    @ApiOperation(name = "重置用户密码")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "用户ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> resetPwd(String id, HttpServletRequest req) {
        return Result.data(sysUserService.resetPwd(id));
    }


    @At("/disabled")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.user.update")
    @SLog(value = "启用禁用:${loginname}")
    @ApiOperation(name = "启用禁用")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "id", description = "主键ID", required = true),
                    @ApiFormParam(name = "loginname", description = "用户姓名", required = true),
                    @ApiFormParam(name = "disabled", description = "disabled=true禁用", required = true)
            }
    )
    @ApiResponses
    public Result<?> changeDisabled(@Param("id") String id, @Param("loginname") String loginname, @Param("disabled") boolean disabled, HttpServletRequest req) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(loginname)) {
            return Result.error("超级管理员不可禁用");
        }
        int res = sysUserService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        sysUserService.cacheRemove(id);
        if (res > 0) {
            if (disabled) {
                req.setAttribute("_slog_msg", "禁用");
            } else {
                req.setAttribute("_slog_msg", "启用");
            }
            return Result.success();
        }
        return Result.error();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("sys.manage.user.delete")
    @SLog(value = "删除用户:${loginname}")
    @ApiOperation(name = "删除用户")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiFormParams(
            {
                    @ApiFormParam(name = "loginname", description = "用户名", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, @Param("loginname") String loginname, HttpServletRequest req) {
        if (GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(loginname)) {
            return Result.error("超级管理员不可删除");
        }
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SLog(value = "删除用户:${names}")
    @ApiOperation(name = "删除用户")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "ids", description = "用户ID数组", required = true, check = true),
                    @ApiFormParam(name = "names", description = "用户名称数组", required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.user.delete")
    public Result<?> create(@Param("ids") String[] ids, @Param("names") String[] names) {
        String superadminId = "";
        Sys_user user = sysUserService.fetch(Cnd.where("loginname", "=", GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME));
        if (user != null) {
            superadminId = user.getId();
        }
        if (ids != null) {
            for (String id : ids) {
                if (superadminId.equals(id)) {
                    return Result.error("超级管理员用户不可删除");
                }
                sysUserService.deleteUser(id);
            }
        }
        return Result.success();
    }

    @At("/export")
    @Ok("void")
    @POST
    @ApiOperation(name = "导出用户数据")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "unitPath", example = "", description = "单位PATH"),
                    @ApiImplicitParam(name = "postId", example = "", description = "职务ID"),
                    @ApiImplicitParam(name = "username", example = "", description = "用户姓名"),
                    @ApiImplicitParam(name = "loginname", example = "", description = "用户名"),
                    @ApiImplicitParam(name = "mobile", example = "", description = "手机号码"),
                    @ApiImplicitParam(name = "disabled", example = "", description = "用户状态", type = "boolean"),
                    @ApiImplicitParam(name = "query", example = "", description = "查询关键词"),
                    @ApiImplicitParam(name = "beginTime", example = "", description = "开始时间", type = "long"),
                    @ApiImplicitParam(name = "endTime", example = "", description = "结束时间", type = "long"),
                    @ApiImplicitParam(name = "query", example = "", description = "查询关键词")
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.user.export")
    public void export(@Param("disabled") Boolean disabled, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("mobile") String mobile, @Param("loginname") String loginname, @Param("username") String username, @Param("unitPath") String unitPath, @Param("postId") String postId, @Param("query") String query, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy, HttpServletRequest req, HttpServletResponse response) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(unitPath)) {
            cnd.and("unitPath", "like", unitPath + "%");
        }
        if (Strings.isNotBlank(postId)) {
            cnd.and("postId", "=", postId);
        }
        if (Strings.isNotBlank(username)) {
            cnd.and("username", "like", "%" + username + "%");
        }
        if (Strings.isNotBlank(loginname)) {
            cnd.and("loginname", "like", "%" + loginname + "%");
        }
        if (Strings.isNotBlank(mobile)) {
            cnd.and("mobile", "like", "%" + mobile + "%");
        }
        if (beginTime != null && endTime != null) {
            cnd.and("createdAt", ">=", beginTime);
            cnd.and("createdAt", "<=", endTime);
        }
        if (disabled != null) {
            cnd.and("disabled", "=", disabled);
        }
        if (Strings.isNotBlank(query)) {
            cnd.and(Cnd.exps("loginname", "like", "%" + query + "%").or("username", "like", "%" + query + "%")
                    .or("mobile", "like", "%" + query + "%"));
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        try {
            List<Sys_user> list = sysUserService.query(cnd, "^(unit|post)$");
            ExcelUtil<Sys_user> util = new ExcelUtil<>(Sys_user.class);
            util.exportExcel(response, list, "用户数据");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @At("/importData")
    @Ok("json")
    @POST
    @ApiOperation(name = "导入用户数据")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
                    @ApiFormParam(name = "updateSupport", example = "", description = "是否覆盖"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.user.import")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    public Result<?> importData(@Param("Filedata") TempFile tf, @Param("pwd") String pwd, @Param(value = "updateSupport", df = "false") Boolean updateSupport, HttpServletRequest req, HttpServletResponse response) throws Exception {
        ExcelUtil<Sys_user> util = new ExcelUtil<>(Sys_user.class);
        List<Sys_user> list = util.importExcel(tf.getInputStream());
        return Result.success(sysUserService.importUser(list, pwd, updateSupport, SecurityUtil.getUserId()));
    }

    @At("/importTemplate")
    @Ok("void")
    @POST
    @ApiOperation(name = "导入数据模版")
    @ApiImplicitParams
    @ApiResponses
    @SaCheckLogin
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Sys_user> util = new ExcelUtil<>(Sys_user.class);
        util.importTemplateExcel(response, "用户数据");
    }
}
