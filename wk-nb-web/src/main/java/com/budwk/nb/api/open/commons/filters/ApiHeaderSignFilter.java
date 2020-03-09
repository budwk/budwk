package com.budwk.nb.api.open.commons.filters;

import com.budwk.nb.api.open.commons.sign.ApiSignServer;
import com.budwk.nb.commons.base.Result;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * 通过 Header 参数实现的Sign签名验证拦截器
 * @author wizzer(wizzer.cn) on 2018/6/28.
 */
public class ApiHeaderSignFilter implements ActionFilter {
    private static final Log log = Logs.get();

    @Override
    public View match(ActionContext context) {
        try {
            ApiSignServer apiSignServer = context.getIoc().get(ApiSignServer.class);
            Map<String, Object> paramMap = getHeaderParameterMap(context.getRequest());
            log.debug("paramMap:::\r\n" + Json.toJson(paramMap));
            Result result = apiSignServer.checkSign(paramMap);
            if (result == null) {
                return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(500305, "system.api.error.unkown"));
            }
            if (result.getCode() == 0) {
                return null;
            }
            return new UTF8JsonView(JsonFormat.compact()).setData(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(500300, "system.api.error"));
        }
    }

    private Map<String, Object> getHeaderParameterMap(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String paramName = names.nextElement();
            //过滤掉非签名参数
            if ("appid".equals(paramName) || "sign".equals(paramName) || "nonce".equals(paramName) || "timestamp".equals(paramName)) {
                map.put(paramName, request.getHeader(paramName));
            }
        }
        return map;
    }
}
