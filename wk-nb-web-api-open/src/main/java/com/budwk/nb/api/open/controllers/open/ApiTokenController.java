package com.budwk.nb.api.open.controllers.open;

import com.budwk.nb.api.open.commons.filters.ApiSignFilter;
import com.budwk.nb.api.open.commons.token.ApiTokenServer;
import com.budwk.nb.commons.base.Result;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wizzer on 2018/4/4.
 */
@IocBean
@At("/open/api/token")
@Filters({@By(type = ApiSignFilter.class)})
public class ApiTokenController {
    private final static Log log = Logs.get();
    @Inject
    private PropertiesProxy conf;
    @Inject
    private ApiTokenServer apiTokenServer;
    @Inject
    private RedisService redisService;

    @POST
    @At("/get")
    @Ok("json")
    public Object get(@Param("appid") String appid) {
        try {
            NutMap resmap = new NutMap();
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.HOUR, +2);//设置token有效期为2小时
            date = c.getTime();
            resmap.addv("expires", 7200);//同时把有效期秒数传递给客户端
            resmap.addv("token", apiTokenServer.generateToken(date, appid));
            return Result.success("获取token成功", resmap);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return Result.error(-1, "获取token失败");
        }
    }
}
