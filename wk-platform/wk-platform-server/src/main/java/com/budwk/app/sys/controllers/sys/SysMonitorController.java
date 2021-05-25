package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.budwk.app.sys.commons.oshi.OshiServer;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import lombok.extern.slf4j.Slf4j;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.annotation.Value;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/sys/monitor")
@SLog(tag = "监控中心")
@ApiDefinition(tag = "监控中心")
@Slf4j
public class SysMonitorController {
    @Inject
    private RedisService redisService;

    @Value(name = "monitor.nacos.url")
    protected String nacos_url;

    @Value(name = "monitor.nacos.username")
    protected String nacos_username;

    @Value(name = "monitor.nacos.password")
    protected String nacos_password;

    @Value(name = "monitor.nacos.namespace")
    protected String nacos_namespace;

    private String getAccessToken() {
        String accessToken = redisService.get(RedisConstant.PRE + "nacos:accesstoken");
        if (Strings.isNotBlank(accessToken)) {
            return accessToken;
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("username", nacos_username);
            params.put("nacos_password", nacos_password);
            String res = Http.post(nacos_url + "/v1/auth/login", params, 3000);
            try {
                NutMap nutMap = Json.fromJson(NutMap.class, res);
                accessToken = nutMap.getString("accessToken");
                if (Strings.isNotBlank(accessToken)) {
                    redisService.setex(RedisConstant.PRE + "nacos:accesstoken", nutMap.getInt("tokenTtl", 60), accessToken);
                    return accessToken;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return "";
        }
    }

    @At("/nacos/services")
    @Ok("json")
    @POST
    @ApiOperation(description = "Nacos服务列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "serviceNameParam", description = "服务名称"),
                    @ApiFormParam(name = "groupNameParam", description = "分组名称"),
                    @ApiFormParam(name = "pageNo", description = "页码", example = "1", type = "integer", required = true, check = true),
                    @ApiFormParam(name = "pageSize", description = "页大小", example = "10", type = "integer", required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckRole("sysadmin")
    public Result<?> list(@Param("serviceNameParam") String serviceNameParam, @Param("groupNameParam") String groupNameParam, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("hasIpCount", true);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("accessToken", getAccessToken());
        params.put("namespaceId", nacos_namespace);
        params.put("serviceNameParam", serviceNameParam);
        params.put("groupNameParam", groupNameParam);
        Response res = Http.get(nacos_url + "/v1/ns/catalog/services", params, 3000);
        return Result.data(Json.fromJson(NutMap.class, res.getContent()));
    }

    @At("/nacos/service")
    @Ok("json")
    @POST
    @ApiOperation(description = "Nacos服务详情")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "serviceName", description = "服务名称", required = true, check = true),
                    @ApiFormParam(name = "groupName", description = "分组名称", required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckRole("sysadmin")
    public Result<?> service( @Param("serviceName") String serviceName, @Param("groupName") String groupName) {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", getAccessToken());
        params.put("namespaceId", nacos_namespace);
        params.put("serviceName", serviceName);
        params.put("groupName", groupName);
        Response res = Http.get(nacos_url + "/v1/ns/catalog/service", params, 3000);
        return Result.data(Json.fromJson(NutMap.class, res.getContent()));
    }

    @At("/nacos/detail")
    @Ok("json")
    @POST
    @ApiOperation(description = "Nacos服务实例列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "clusterName", description = "集群名称", df = "DEFAULT", example = "DEFAULT", required = true),
                    @ApiFormParam(name = "serviceName", description = "服务名称", required = true, check = true),
                    @ApiFormParam(name = "groupName", description = "分组名称", required = true, check = true),
                    @ApiFormParam(name = "pageNo", description = "页码", example = "1", type = "integer", required = true, check = true),
                    @ApiFormParam(name = "pageSize", description = "页大小", example = "10", type = "integer", required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckRole("sysadmin")
    public Result<?> detail(@Param(value = "clusterName", df = "DEFAULT") String clusterName, @Param("serviceName") String serviceName, @Param("groupName") String groupName, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("accessToken", getAccessToken());
        params.put("namespaceId", nacos_namespace);
        params.put("clusterName", clusterName);
        params.put("serviceName", serviceName);
        params.put("groupName", groupName);
        Response res = Http.get(nacos_url + "/v1/ns/catalog/instances", params, 3000);
        return Result.data(Json.fromJson(NutMap.class, res.getContent()));
    }

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
        nutMap.addv("dbSize",redisService.dbSize());
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
                        .addv("mem", OshiServer.getMemInfo(hal.getMemory()))
                        .addv("sys", OshiServer.getSysInfo())
                        .addv("files", OshiServer.getSysFiles(si.getOperatingSystem()))
        );
    }
}
