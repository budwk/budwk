package com.budwk.nb.web.controllers.open.test;

import com.budwk.nb.base.result.Result;
import com.budwk.nb.web.commons.ext.api.filters.ApiSignFilter;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

/**
 * @author wizzer(wizzer.cn) on 2018/4/4.
 */
@IocBean
@At("/open/api/test")
@Filters({@By(type = ApiSignFilter.class)})
public class ApiTestSignController {
    private final static Log log = Logs.get();

    @At("/test2")
    @Ok("json")
    @POST
    public Object test2(@Param("openid") String openid) {
        return Result.success("执行成功","openid:::"+openid);
    }
}
