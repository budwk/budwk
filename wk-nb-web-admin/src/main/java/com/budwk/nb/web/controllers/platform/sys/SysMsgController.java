package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.sys.enums.SysMsgTypeEnum;
import com.budwk.nb.sys.services.SysMsgService;
import com.budwk.nb.sys.services.SysMsgUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

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
     * @apiSuccess {Object} data  多语言字符串
     */
    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.config.lang")
    public Object list(@Param("type") String type, @Param("title") String title, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(type)) {
                cnd.and("type", "=", type);
            }
            if (Strings.isNotBlank(title)) {
                cnd.and(Cnd.likeEX("title", title));
            }
            cnd.and("delFlag", "=", false);
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

}
