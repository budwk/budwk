package com.budwk.starter.apiauth.filter;

import com.budwk.starter.apiauth.service.ApiSignServer;
import com.budwk.starter.apiauth.wrapper.RequestWrapper;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ApiSignFilter implements ActionFilter {

    @Override
    public View match(ActionContext context) {
        try {
            ApiSignServer apiSignServer = context.getIoc().get(ApiSignServer.class);
            Map<String, Object> paramMap = getParameterMap(context.getRequest());
            Result<?> result = apiSignServer.checkSign(paramMap);
            if (result == null) {
                return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(ResultCode.SIGIN_ERROR));
            }
            if (result.isSuccess()) {
                return null;
            }
            return new UTF8JsonView(JsonFormat.compact()).setData(result);
        } catch (Exception e) {
            log.error("签名校验出错", e);
            return new UTF8JsonView(JsonFormat.compact()).setData(Result.error());
        }
    }

    private Map<String, Object> getParameterMap(HttpServletRequest request) {
        String contentType = request.getContentType();
        Map<String, Object> map = new HashMap<>();
        if (Strings.isNotBlank(contentType) && contentType.contains("application/json")) {
            // json数据
            String json = new RequestWrapper(request).getBodyString();
            map = Json.fromJson(NutMap.class, json);
        }
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String paramName = names.nextElement();
            map.put(paramName, request.getParameter(paramName));
        }
        return map;
    }
}
