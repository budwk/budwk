package com.budwk.nb.web.commons.ext.api.filters;

import com.budwk.nb.base.result.Result;
import com.budwk.nb.web.commons.ext.api.token.ApiTokenServer;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;


/**
 * JWT Token拦截器
 * @author wizzer(wizzer.cn) on 2016/8/11.
 */
public class ApiTokenFilter implements ActionFilter {
    private static final Log log = Logs.get();

    @Override
    public View match(ActionContext context) {
        try {
            String appid = Strings.sNull(context.getRequest().getParameter("appid"));
            String token = Strings.sNull(context.getRequest().getParameter("token"));
            if (!context.getIoc().get(ApiTokenServer.class).verifyToken(appid, token)) {
                return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(-2, "token失效,请重新获取"));
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(5001001, "系统异常"));
        }
    }
}
