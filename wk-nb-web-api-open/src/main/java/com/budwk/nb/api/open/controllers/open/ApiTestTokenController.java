package com.budwk.nb.api.open.controllers.open;

import com.budwk.nb.api.open.commons.filters.ApiTokenFilter;
import com.budwk.nb.commons.base.Result;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

/**
 * @author wizzer(wizzer@qq.com) on 2018/4/4.
 */
@IocBean
@At("/open/api/test")
@Filters({@By(type = ApiTokenFilter.class)})
public class ApiTestTokenController {
    private final static Log log = Logs.get();

    @At("/test1")
    @Ok("json")
    @POST
    public Object test1(@Param("openid") String openid) {
        return Result.success("执行成功","openid:::"+openid);
    }
}
