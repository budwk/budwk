package com.budwk.nb.web.controllers.platform.wx;

import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.ext.wx.WxService;
import com.budwk.nb.wx.models.Wx_config;
import com.budwk.nb.wx.models.Wx_menu;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.wx.services.WxMenuService;
import com.budwk.nb.wx.services.WxReplyService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
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
@At("/api/{version}/platform/wx/conf/menu")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_菜单配置")}, servers = {@Server(url = "/")})
public class WxMenuController {
    private static final Log log = Logs.get();

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
    @RequiresAuthentication
    @Operation(
            tags = "微信_菜单配置", summary = "表格展开下级菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "pid", description = "父级ID")
            }
    )
    public Object child(String wxid, @Param("pid") String pid, HttpServletRequest req) {
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
        return Result.success().addData(treeList);
    }

    @At("/tree/{wxid}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "微信_菜单配置", summary = "获取菜单树结构",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "pid", description = "父级ID")
            }
    )
    public Object tree(String wxid, @Param("pid") String pid, HttpServletRequest req) {
        try {
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid)) {
                NutMap root = NutMap.NEW().addv("value", "root").addv("label", Mvcs.getMessage(req, "system.commons.txt.parentRoot")).addv("leaf", true);
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
            return Result.success().addData(treeList);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.menu.create")
    @SLog(tag = "添加菜单", msg = "菜单名称:${menu.menuName}")
    @Operation(
            tags = "微信_菜单配置", summary = "添加菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "parentId", description = "父级ID")
            },
            implementation = Wx_menu.class
    )
    public Object create(@Param("..") Wx_menu menu, @Param(value = "parentId", df = "") String parentId, HttpServletRequest req) {
        try {
            if ("root".equals(parentId)) {
                parentId = "";
            }
            if (Strings.isBlank(menu.getWxid())) {
                return Result.error("wx.conf.menu.error.wxid");
            }
            int count = wxMenuService.count(Cnd.where("wxid", "=", Strings.sBlank(menu.getWxid())).and("parentId", "=", Strings.sBlank(parentId)));
            if (Strings.isBlank(parentId) && count > 2) {
                return Result.error("wx.conf.menu.error.class1");
            }
            if (!Strings.isBlank(parentId) && count > 4) {
                return Result.error("wx.conf.menu.error.class2");
            }
            menu.setCreatedBy(StringUtil.getPlatformUid());
            menu.setUpdatedBy(StringUtil.getPlatformUid());
            wxMenuService.save(menu, parentId);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "微信_菜单配置", summary = "获取微信菜单信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object get(String id, HttpServletRequest req) {
        try {
            return Result.success().addData(wxMenuService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.menu.update")
    @SLog(tag = "修改菜单", msg = "菜单名称:${menu.menuName}")
    @Operation(
            tags = "微信_菜单配置", summary = "修改菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_menu.class
    )
    public Object update(@Param("..") Wx_menu menu, HttpServletRequest req) {
        try {
            menu.setUpdatedBy(StringUtil.getPlatformUid());
            wxMenuService.updateIgnoreNull(menu);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("wx.conf.menu.delete")
    @SLog(tag = "删除菜单")
    @Operation(
            tags = "微信_菜单配置", summary = "删除菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String id, HttpServletRequest req) {
        try {
            Wx_menu menu = wxMenuService.fetch(id);
            if (menu == null) {
                return Result.error("system.error.noData");
            }
            wxMenuService.deleteAndChild(menu);
            req.setAttribute("_slog_msg", String.format("%s", menu.getMenuName()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/push/{wxid}")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.menu.push")
    @SLog(tag = "推送菜单")
    @Operation(
            tags = "微信_菜单配置", summary = "推送菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu.push")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object pushMenu(String wxid, HttpServletRequest req) {
        try {
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
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @At("/list_keyword")
    @POST
    @Ok("json:full")
    @RequiresPermissions("wx.conf.menu")
    @Operation(
            tags = "微信_菜单配置", summary = "获取关键词",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "wxid", description = "微信ID")
            }
    )
    public Object getKeyword(@Param("wxid") String wxid) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!Strings.isBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            cnd.and("type", "=", "keyword");
            return Result.success().addData(wxReplyService.query(cnd));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get_sort_tree/{wxid}")
    @GET
    @Ok("json")
    @RequiresPermissions("wx.conf.menu")
    @Operation(
            tags = "微信_菜单配置", summary = "获取待排序菜单树",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getSortTree(String wxid, HttpServletRequest req) {
        try {
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
            return Result.success().addData(getTree(menuMap, ""));
        } catch (Exception e) {
            return Result.error();
        }
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
    @RequiresPermissions("wx.conf.menu")
    @Operation(
            tags = "微信_菜单配置", summary = "微信菜单排序",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.menu.sort")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "ids", description = "菜单ID数组")
            }
    )
    public Object sort(String wxid, @Param("ids") String ids, HttpServletRequest req) {
        try {
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
        } catch (Exception e) {
            return Result.error();
        }
    }


}
