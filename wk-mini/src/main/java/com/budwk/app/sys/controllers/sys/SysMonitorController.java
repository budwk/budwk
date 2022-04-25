package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.budwk.app.sys.commons.oshi.OshiServer;
import com.budwk.starter.common.openapi.annotation.ApiDefinition;
import com.budwk.starter.common.openapi.annotation.ApiFormParams;
import com.budwk.starter.common.openapi.annotation.ApiOperation;
import com.budwk.starter.common.openapi.annotation.ApiResponses;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/monitor")
@SLog(tag = "监控中心")
@ApiDefinition(tag = "监控中心")
@Slf4j
public class SysMonitorController {
    @Inject
    private RedisService redisService;

    @At("/redis/info")
    @Ok("json")
    @GET
    @ApiOperation(description = "Redis信息")
    @ApiFormParams
    @ApiResponses
    @SaCheckRole("sysadmin")
    public Result<?> redisInfo() {
        NutMap nutMap = NutMap.NEW();
        String info = redisService.info();
        String[] infos = Strings.splitIgnoreBlank(info, "\r\n");
        for (String str : infos) {
            if (Strings.sNull(str).contains(":")) {
                String[] v = Strings.splitIgnoreBlank(str, ":");
                nutMap.put(v[0], v[1]);
            }
        }
        nutMap.addv("dbSize", redisService.dbSize());
        return Result.data(nutMap);
    }

    @At("/server/info")
    @Ok("json")
    @GET
    @ApiOperation(description = "服务器信息")
    @ApiFormParams
    @ApiResponses
    @SaCheckRole("sysadmin")
    public Result<?> serverInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        return Result.data(
                NutMap.NEW().addv("cpu", OshiServer.getCpu(hal.getProcessor()))
                        .addv("jvm", OshiServer.getJvmInfo())
                        .addv("mem", OshiServer.getMemInfo(hal.getMemory()))
                        .addv("sys", OshiServer.getSysInfo())
                        .addv("files", OshiServer.getSysFiles(si.getOperatingSystem()))
        );
    }
}
