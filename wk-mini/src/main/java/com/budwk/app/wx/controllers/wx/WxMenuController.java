package com.budwk.app.wx.controllers.wx;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.models.Wx_config;
import com.budwk.app.wx.models.Wx_menu;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.app.wx.services.WxMenuService;
import com.budwk.app.wx.services.WxReplyService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.weixin.bean.WxMenu;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/4
 */
@IocBean
@At("/admin/conf/menu")
@Ok("json")
@ApiDefinition(tag = "微信菜单配置")
@SLog(tag = "微信菜单配置")
@Slf4j
public class WxMenuController {

    @Inject
    private WxConfigService wxConfigService;

    @Inject
    private WxMenuService wxMenuService;

    @Inject
    private WxReplyService wxReplyService;

    @Inject
    private WxService wxService;

    @At("/child/{wxid}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "表格展开下级菜单")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "wxid", in = ParamIn.PATH),
                    @ApiImplicitParam(name = "pid", description = "父级ID")
            }
    )
    @ApiResponses
    public Result<?> child(String wxid, @Param("pid") String pid, HttpServletRequest req) {
        List<Wx_menu> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.and("wxid", "=", wxid);
        cnd.asc("location").asc("path");
        list = wxMenuService.query(cnd);
        for (Wx_menu menu : list) {
            if (wxMenuService.count(Cnd.where("parentId", "=", menu.getId())) > 0) {
                menu.setHasChildren(true);
            }
            NutMap map = Lang.obj2nutmap(menu);
            map.addv("expanded", false);
            map.addv("children", new ArrayList<>());
            treeList.add(map);
        }
        return Result.data(treeList);
    }

    @At("/tree/{wxid}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取菜单树结构")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "wxid", in = ParamIn.PATH),
                    @ApiImplicitParam(name = "pid", description = "父级ID")
            }
    )
    @ApiResponses
    public Result<?> tree(String wxid, @Param("pid") String pid, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("value", "root").addv("label", "默认顶级").addv("leaf", true);
            treeList.add(root);
        }
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.and("wxid", "=", wxid);
        cnd.asc("location").asc("path");
        List<Wx_menu> list = wxMenuService.query(cnd);
        for (Wx_menu menu : list) {
            NutMap map = NutMap.NEW().addv("value", menu.getId()).addv("label", menu.getMenuName());
            if (menu.isHasChildren()) {
                map.addv("children", new ArrayList<>());
                map.addv("leaf", false);
            } else {
                map.addv("leaf", true);
            }
            treeList.add(map);
        }
        return Result.data(treeList);
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.menu.create")
    @SLog("添加菜单:${menu.menuName}")
    @ApiOperation(description = "添加菜单")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "parentId", description = "父级ID")
            },
            implementation = Wx_menu.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_menu menu, @Param(value = "parentId", df = "") String parentId, HttpServletRequest req) {
        if ("root".equals(parentId)) {
            parentId = "";
        }
        if (Strings.isBlank(menu.getWxid())) {
            return Result.error("请选择微信公众号");
        }
        int count = wxMenuService.count(Cnd.where("wxid", "=", Strings.sBlank(menu.getWxid())).and("parentId", "=", Strings.sBlank(parentId)));
        if (Strings.isBlank(parentId) && count > 2) {
            return Result.error("只可设置三个一级菜单");
        }
        if (!Strings.isBlank(parentId) && count > 4) {
            return Result.error("只可设置五个二级菜单");
        }
        menu.setCreatedBy(SecurityUtil.getUserId());
        wxMenuService.save(menu, parentId);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取菜单信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        return Result.data(wxMenuService.fetch(id));
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.menu.update")
    @SLog("修改菜单:${menu.menuName}")
    @ApiOperation(description = "修改菜单")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "parentId", description = "父级ID")
            },
            implementation = Wx_menu.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_menu menu, HttpServletRequest req) {
        menu.setUpdatedBy(SecurityUtil.getUserId());
        wxMenuService.updateIgnoreNull(menu);
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.conf.menu.delete")
    @SLog(tag = "删除菜单:")
    @ApiOperation(description = "获取菜单信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_menu menu = wxMenuService.fetch(id);
        if (menu == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxMenuService.deleteAndChild(menu);
        req.setAttribute("_slog_msg", String.format("%s", menu.getMenuName()));
        return Result.success();
    }

    @At("/push/{wxid}")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.menu.push")
    @SLog(tag = "推送菜单")
    @ApiOperation(description = "推送菜单")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> pushMenu(String wxid, HttpServletRequest req) {
        Wx_config config = wxConfigService.fetch(wxid);
        WxApi2 wxApi2 = wxService.getWxApi2(wxid);
        List<Wx_menu> list = wxMenuService.query(Cnd.where("wxid", "=", wxid).asc("location"));
        req.setAttribute("_slog_msg", String.format("微信公众号:%s", config.getAppname()));
        List<Wx_menu> firstMenus = new ArrayList<>();
        Map<String, List<Wx_menu>> secondMenus = new HashMap<>();
        for (Wx_menu menu : list) {
            if (menu.getPath().length() > 4) {
                List<Wx_menu> s = secondMenus.get(wxMenuService.getParentPath(menu.getPath()));
                if (s == null) s = new ArrayList<>();
                s.add(menu);
                secondMenus.put(wxMenuService.getParentPath(menu.getPath()), s);
            } else if (menu.getPath().length() == 4) {
                firstMenus.add(menu);
            }
        }
        List<WxMenu> m1 = new ArrayList<>();
        for (Wx_menu firstMenu : firstMenus) {
            WxMenu xm1 = new WxMenu();
            if (firstMenu.isHasChildren()) {
                List<WxMenu> m2 = new ArrayList<>();
                xm1.setName(firstMenu.getMenuName());
                if (secondMenus.get(firstMenu.getPath()).size() > 0) {
                    for (Wx_menu secondMenu : secondMenus.get(firstMenu.getPath())) {
                        WxMenu xm2 = new WxMenu();
                        if ("view".equals(secondMenu.getMenuType())) {
                            xm2.setType(secondMenu.getMenuType());
                            xm2.setUrl(secondMenu.getUrl());
                            xm2.setName(secondMenu.getMenuName());
                        } else if ("click".equals(secondMenu.getMenuType())) {
                            xm2.setType(secondMenu.getMenuType());
                            xm2.setKey(secondMenu.getMenuKey());
                            xm2.setName(secondMenu.getMenuName());
                        } else if ("miniprogram".equals(secondMenu.getMenuType())) {
                            xm2.setType(secondMenu.getMenuType());
                            xm2.setName(secondMenu.getMenuName());
                            xm2.setUrl(secondMenu.getUrl());
                            xm2.setAppid(secondMenu.getAppid());
                            xm2.setPagepath(secondMenu.getPagepath());
                        } else {
                            xm2.setName(secondMenu.getMenuName());
                            xm2.setType("click");
                            xm2.setKey(secondMenu.getMenuName());
                        }
                        m2.add(xm2);
                    }
                    xm1.setSubButtons(m2);
                }
                m1.add(xm1);
            } else {
                WxMenu xm2 = new WxMenu();
                if ("view".equals(firstMenu.getMenuType())) {
                    xm2.setType(firstMenu.getMenuType());
                    xm2.setUrl(firstMenu.getUrl());
                    xm2.setName(firstMenu.getMenuName());
                } else if ("click".equals(firstMenu.getMenuType())) {
                    xm2.setType(firstMenu.getMenuType());
                    xm2.setKey(firstMenu.getMenuKey());
                    xm2.setName(firstMenu.getMenuName());
                } else if ("miniprogram".equals(firstMenu.getMenuType())) {
                    xm2.setType(firstMenu.getMenuType());
                    xm2.setName(firstMenu.getMenuName());
                    xm2.setUrl(firstMenu.getUrl());
                    xm2.setAppid(firstMenu.getAppid());
                    xm2.setPagepath(firstMenu.getPagepath());
                } else {
                    xm2.setName(firstMenu.getMenuName());
                    xm2.setType("click");
                    xm2.setKey(firstMenu.getMenuName());
                }
                m1.add(xm2);
            }
        }
        WxResp wxResp = wxApi2.menu_create(m1);
        if (wxResp.errcode() != 0) {
            return Result.error(wxResp.errmsg());
        }
        return Result.success();
    }

    @At("/list_keyword")
    @POST
    @Ok("json:full")
    @SaCheckPermission("wx.conf.menu")
    @ApiOperation(description = "获取关键词")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", description = "wxid")
            }
    )
    @ApiResponses
    public Result<?> getKeyword(@Param("wxid") String wxid) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        cnd.and("type", "=", "keyword");
        return Result.data(wxReplyService.query(cnd));
    }

    @At("/get_sort_tree/{wxid}")
    @GET
    @Ok("json")
    @SaCheckPermission("wx.conf.menu")
    @ApiOperation(description = "获取待排序菜单树")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getSortTree(String wxid, HttpServletRequest req) {
        List<Wx_menu> list = wxMenuService.query(Cnd.where("wxid", "=", wxid).asc("location").asc("path"));
        NutMap menuMap = NutMap.NEW();
        for (Wx_menu unit : list) {
            List<Wx_menu> list1 = menuMap.getList(unit.getParentId(), Wx_menu.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(unit);
            menuMap.put(unit.getParentId(), list1);
        }
        return Result.data(getTree(menuMap, ""));
    }

    private List<NutMap> getTree(NutMap menuMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Wx_menu> subList = menuMap.getList(pid, Wx_menu.class);
        for (Wx_menu menu : subList) {
            NutMap map = Lang.obj2nutmap(menu);
            map.put("label", menu.getMenuName());
            if (menu.isHasChildren() || (menuMap.get(menu.getId()) != null)) {
                map.put("children", getTree(menuMap, menu.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    @At("/sort/{wxid}")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.menu")
    @ApiOperation(description = "微信菜单排序")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", description = "菜单ID数组")
            }
    )
    @ApiResponses
    public Result<?> sort(String wxid, @Param("ids") String ids, HttpServletRequest req) {
        String[] menuIds = StringUtils.split(ids, ",");
        int i = 0;
        wxMenuService.execute(Sqls.create("update wx_menu set location=0 where wxid=@wxid").setParam("wxid", wxid));
        for (String s : menuIds) {
            if (!Strings.isBlank(s)) {
                wxMenuService.update(org.nutz.dao.Chain.make("location", i), Cnd.where("id", "=", s));
                i++;
            }
        }
        return Result.success();
    }
}
