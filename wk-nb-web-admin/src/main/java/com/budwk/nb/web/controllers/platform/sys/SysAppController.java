package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.commons.base.Result;
import com.budwk.nb.sys.services.SysAppConfService;
import com.budwk.nb.sys.services.SysAppListService;
import com.budwk.nb.sys.services.SysAppTaskService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelProperty;
import org.nutz.boot.starter.logback.exts.loglevel.LoglevelService;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.ApiVersion;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/1/10
 */
@IocBean
@At("/api/{version}/platform/sys/app")
@Ok("json")
@ApiVersion("1.0.0")
public class SysAppController {
    private static final Log log = Logs.get();
    @Inject
    private LoglevelService loglevelService;
    @Inject
    private SysAppListService sysAppListService;
    @Inject
    private SysAppConfService sysAppConfService;
    @Inject
    private SysAppTaskService sysAppTaskService;
    @Inject
    private RedisService redisService;

    /**
     * @api {post} /api/1.0.0/platform/sys/app/host_data 获取主机数据
     * @apiName host_data
     * @apiGroup SYS_APP
     * @apiPermission sys.server.app
     * @apiVersion 1.0.0
     * @apiParam {String} hostName   主机名
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  主机数据
     */
    @At("/host_data")
    @Ok("json:full")
    @RequiresPermissions("sys.server.app")
    @SuppressWarnings("unchecked")
    public Object getHostData(@Param("hostName") String hostName) {
        try {
            List<NutMap> hostList = new ArrayList<>();
            NutMap map = loglevelService.getData();
            List<LoglevelProperty> dataList = new ArrayList<>();
            //对数据进行整理,获得左侧主机列表及右侧实例数据
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                List<LoglevelProperty> list = (List) entry.getValue();
                for (LoglevelProperty property : list) {
                    NutMap nutMap = NutMap.NEW().addv("hostName", property.getHostName()).addv("hostAddress", property.getHostAddress());
                    if (!hostList.contains(nutMap))
                        hostList.add(nutMap);
                    if (Strings.isBlank(hostName) || (Strings.isNotBlank(hostName) && property.getHostName().equals(hostName))) {
                        dataList.add(property);
                    }
                }
            }
            return Result.success().addData(NutMap.NEW().addv("hostList", hostList).addv("appList", dataList));
        } catch (Exception e) {
            return Result.error();
        }
    }
}
