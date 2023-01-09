package com.budwk.app.cms.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.cms.enums.CmsChannelType;
import com.budwk.app.cms.models.Cms_channel;
import com.budwk.app.cms.models.Cms_site;
import com.budwk.app.cms.services.CmsChannelService;
import com.budwk.app.cms.services.CmsSiteService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/10
 */
@IocBean
@At("/cms/admin/channel")
@ApiDefinition(tag = "CMS栏目管理")
@SLog(tag = "CMS栏目管理")
@Slf4j
public class CmsChannelController {

    @Inject
    private CmsChannelService cmsChannelService;
    @Inject
    private CmsSiteService cmsSiteService;

    @At("/list_site")
    @GET
    @Ok("json:{actived:'code|msg|data|id|site_name',ignoreNull:true}")
    @SaCheckLogin
    @ApiOperation(name = "站点列表")
    @ApiResponses(
            implementation = Cms_site.class
    )
    public Result<?> listSite(HttpServletRequest req) {
        return Result.success().addData(cmsSiteService.query());
    }

    @At("/get_type")
    @Ok("json")
    @GET
    @SaCheckLogin
    @ApiOperation(name = "获取栏目类型")
    @ApiResponses
    public Result<?> getType() {
        return Result.data(CmsChannelType.values());
    }

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue3树形列表查询")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "siteId", example = "", description = "所属站点", required = true, check = true)
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckLogin
    public Result<?> list(@Param("siteId") String siteId) {
        Cnd cnd = Cnd.NEW();
        cnd.and("siteId", "=", siteId);
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(cmsChannelService.query(cnd));
    }

    @At("/child/{siteId}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(name = "Vue2获取子级栏目树表数据")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "pid", example = "", description = "父级ID")
            }
    )
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "siteId", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> child(String siteId, @Param("pid") String pid, HttpServletRequest req) {
        List<Cms_channel> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.and("siteId", "=", siteId);
        cnd.asc("location").asc("path");
        list = cmsChannelService.query(cnd);
        for (Cms_channel channel : list) {
            if (cmsChannelService.count(Cnd.where("parentId", "=", channel.getId())) > 0) {
                channel.setHasChildren(true);
            }
            NutMap map = Lang.obj2nutmap(channel);
            map.addv("expanded", false);
            map.addv("children", new ArrayList<>());
            treeList.add(map);
        }
        return Result.data(treeList);
    }

    @At("/tree/{siteId}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(name = "获取子级栏目树数据")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "pid", example = "", description = "父级ID")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "siteId", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> tree(String siteId, @Param("pid") String pid, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("value", "root").addv("label", "根栏目").addv("leaf", true);
            treeList.add(root);
        }
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.and("siteId", "=", siteId);
        cnd.asc("location").asc("path");
        List<Cms_channel> list = cmsChannelService.query(cnd);
        for (Cms_channel menu : list) {
            NutMap map = NutMap.NEW().addv("value", menu.getId()).addv("label", menu.getName());
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
    @SaCheckPermission("cms.content.channel.create")
    @SLog(value = "新建栏目:${channel.name}")
    @ApiOperation(name = "新建栏目")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "parentId", example = "", description = "父级ID")
            },
            implementation = Cms_channel.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Cms_channel channel, @Param(value = "parentId", df = "") String parentId, HttpServletRequest req) {
        if ("root".equals(parentId)) {
            parentId = "";
        }
        int codeCount = cmsChannelService.count(Cnd.where("code", "=", Strings.sNull(channel.getCode())));
        if (codeCount > 0) {
            return Result.error("栏目标识");
        }
        channel.setCreatedBy(SecurityUtil.getUserId());
        channel.setUpdatedBy(SecurityUtil.getUserId());
        cmsChannelService.save(channel, parentId);
        cmsChannelService.cacheClear();
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckPermission("cms.content.channel")
    @ApiOperation(name = "获取栏目信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, check = true, required = true)
            }
    )
    @ApiResponses(
            implementation = Cms_channel.class
    )
    public Result<?> get(String id, HttpServletRequest req) {
        Cms_channel channel = cmsChannelService.fetch(id);
        NutMap map = Lang.obj2nutmap(channel);
        map.put("parentName", "-");
        map.put("siteName", "-");
        if (Strings.isNotBlank(channel.getParentId())) {
            map.put("parentName", cmsChannelService.fetch(channel.getParentId()).getName());
        }
        if (Strings.isNotBlank(channel.getSiteId())) {
            map.put("siteName", cmsSiteService.fetch(channel.getSiteId()).getSite_name());
        }
        return Result.data(map);
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.content.channel.update")
    @SLog(value = "修改栏目:${channel.name}")
    @ApiOperation(name = "修改栏目")
    @ApiFormParams(
            implementation = Cms_channel.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Cms_channel channel, HttpServletRequest req) {
        Cms_channel dbChannel = cmsChannelService.fetch(channel.getId());
        if (!Strings.sNull(channel.getCode()).equals(dbChannel.getCode())) {
            int codeCount = cmsChannelService.count(Cnd.where("code", "=", Strings.sNull(channel.getCode())));
            if (codeCount > 0) {
                return Result.error(ResultCode.FAILURE);
            }
        }
        channel.setUpdatedBy(SecurityUtil.getUserId());
        cmsChannelService.updateIgnoreNull(channel);
        cmsChannelService.cacheClear();
        return Result.success();
    }

    @At("/delete/{id}")
    @DELETE
    @Ok("json")
    @SaCheckPermission("cms.content.channel.delete")
    @SLog(value = "删除栏目")
    @ApiOperation(name = "删除栏目")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Cms_channel channel = cmsChannelService.fetch(id);
        req.setAttribute("_slog_msg", String.format("栏目名称:%s", channel.getName()));
        cmsChannelService.deleteAndChild(channel);
        cmsChannelService.cacheClear();
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @SaCheckPermission("cms.content.channel.update")
    @SLog(value = "启用禁用栏目")
    @ApiOperation(name = "启用禁用栏目")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "id", description = "主键", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    @ApiResponses
    public Result<?> changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        int res = cmsChannelService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        if (res > 0) {
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            cmsChannelService.cacheClear();
            return Result.success();
        }
        return Result.error();
    }

    @At("/get_sort_tree/{siteId}")
    @Ok("json")
    @GET
    @SaCheckPermission("cms.content.channel")
    @ApiOperation(name = "Vue2获取待排序栏目树")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "siteId", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> getSortTree(String siteId, HttpServletRequest req) {
        List<Cms_channel> list = cmsChannelService.query(Cnd.where("siteId", "=", siteId).asc("location").asc("path"));
        NutMap nutMap = NutMap.NEW();
        for (Cms_channel channel : list) {
            List<Cms_channel> list1 = nutMap.getList(channel.getParentId(), Cms_channel.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(channel);
            nutMap.put(Strings.sNull(channel.getParentId()), list1);
        }
        return Result.data(getTree(nutMap, ""));
    }

    private List<NutMap> getTree(NutMap nutMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Cms_channel> subList = nutMap.getList(pid, Cms_channel.class);
        for (Cms_channel channel : subList) {
            NutMap map = Lang.obj2nutmap(channel);
            map.put("label", channel.getName());
            if (channel.isHasChildren() || (nutMap.get(channel.getId()) != null)) {
                map.put("children", getTree(nutMap, channel.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    @At("/sort/{siteId}")
    @Ok("json")
    @POST
    @SaCheckPermission("cms.content.channel")
    @ApiOperation(name = "提交栏目排序数据")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", description = "ID数组")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "siteId", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> sortDo(String siteId, @Param("ids") String ids, HttpServletRequest req) {
        String[] unitIds = StringUtils.split(ids, ",");
        int i = 0;
        cmsChannelService.update(Chain.make("location", 0), Cnd.NEW());
        for (String id : unitIds) {
            if (!Strings.isBlank(id)) {
                cmsChannelService.update(Chain.make("location", i), Cnd.where("id", "=", id).and("siteId", "=", siteId));
                i++;
            }
        }
        cmsChannelService.cacheClear();
        return Result.success();
    }

}
