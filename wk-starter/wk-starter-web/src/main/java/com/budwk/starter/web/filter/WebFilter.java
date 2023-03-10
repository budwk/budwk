package com.budwk.starter.web.filter;

import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.common.utils.WebUtil;
import com.budwk.starter.web.wrapper.RequestWrapper;
import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
public class WebFilter implements Filter {
    protected static Log log;

    private boolean xssEnable;
    private boolean sqlEnable;
    private List<String> sqlIgnoreUrls;
    private boolean corsEnable;
    private String corsOrigin;
    private int corsMaxAge;
    private String corsMethods;
    private String corsHeaders;

    public WebFilter(boolean xssEnable, boolean sqlEnable, List<String> sqlIgnoreUrls,
                     boolean corsEnable,
                     String corsOrigin,
                     int corsMaxAge,
                     String corsMethods,
                     String corsHeaders) {
        this.xssEnable = xssEnable;
        this.sqlEnable = sqlEnable;
        this.sqlIgnoreUrls = sqlIgnoreUrls;
        this.corsEnable = corsEnable;
        this.corsOrigin = corsOrigin;
        this.corsMaxAge = corsMaxAge;
        this.corsMethods = corsMethods;
        this.corsHeaders = corsHeaders;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log = Logs.getLog(this.getClass());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.setHeader("X-Powered-By", GlobalConstant.X_POWER_BY);
        // 添加链路id
        String traceId = httpRequest.getHeader(GlobalConstant.WK_TRACE_ID);
        MDC.put(GlobalConstant.WK_LOG_TRACE_ID, Strings.sNull(traceId));
        // 跨域设置
        if (corsEnable) {
            httpResponse.addHeader("Access-Control-Allow-Origin", corsOrigin);
            httpResponse.addHeader("Access-Control-Max-Age", "" + corsMaxAge);
            httpResponse.addHeader("Access-Control-Allow-Methods", corsMethods);
            httpResponse.addHeader("Access-Control-Allow-Headers",
                    corsHeaders);
        }
        // header参数检查
        String userToken = Strings.sNull(httpRequest.getHeader("wk-user-token"));
        if (Strings.isNotBlank(userToken) && !userToken.matches("\\w+")) {
            WebUtil.rendAjaxResp(httpRequest, httpResponse, Result.error(ResultCode.HEADER_PARAM_TOKEN_ERROR));
            return;
        }
        String memberToken = Strings.sNull(httpRequest.getHeader("wk-member-token"));
        if (Strings.isNotBlank(memberToken) && !memberToken.matches("\\w+")) {
            WebUtil.rendAjaxResp(httpRequest, httpResponse, Result.error(ResultCode.HEADER_PARAM_MEMBER_ERROR));
            return;
        }
        String lang = Strings.sNull(httpRequest.getHeader("lang"));
        if (Strings.isNotBlank(lang) && lang.length() > 10) {
            WebUtil.rendAjaxResp(httpRequest, httpResponse, Result.error(ResultCode.HEADER_PARAM_LANG_ERROR));
            return;
        }
        String appId = Strings.sNull(httpRequest.getHeader("appId"));
        if (Strings.isNotBlank(appId) && !appId.matches("\\w+")) {
            WebUtil.rendAjaxResp(httpRequest, httpResponse, Result.error(ResultCode.HEADER_PARAM_APPID_ERROR));
            return;
        }
        // application/json 数据时,request.getInputStream() 要读取两次以便在适配器之前进行表单验证
        if (Strings.sNull(httpRequest.getHeader("Content-Type")).toLowerCase().contains("application/json")) {
            // 传参检测
            ServletRequest requestWrapper = new RequestWrapper(httpRequest);
            if (checkUrl(httpRequest) && checkParamsJson(requestWrapper)) {
                WebUtil.rendAjaxResp(httpRequest, httpResponse, Result.error(ResultCode.XSS_SQL_ERROR));
                return;
            }
            filterChain.doFilter(requestWrapper, httpResponse);
        } else {
            if (checkUrl(httpRequest) && checkParams(httpRequest)) {
                WebUtil.rendAjaxResp(httpRequest, httpResponse, Result.error(ResultCode.XSS_SQL_ERROR));
                return;
            }
            filterChain.doFilter(httpRequest, httpResponse);
        }
    }

    @Override
    public void destroy() {

    }

    private boolean checkUrl(HttpServletRequest req) {
        String path = Mvcs.getRequestPath(req);
        return !sqlIgnoreUrls.contains(path);
    }

    private boolean checkParams(HttpServletRequest req) {
        Iterator<String[]> values = req.getParameterMap().values().iterator();
        // 因为是游标所以要重新获取
        Iterator<String[]> values2 = req.getParameterMap().values().iterator();
        boolean isError = false;
        String regExSql = "select,update,and,or,delete,insert,trancate,char,chr,into,substr,declare,exec,count,drop,execute";
        String regExXss = "script,iframe";
        if (sqlEnable) {
            //SQL过滤
            while (values.hasNext()) {
                String[] valueArray = values.next();
                for (int i = 0; i < valueArray.length; i++) {
                    String value = valueArray[i].toLowerCase();
                    //分拆关键字
                    String[] strs = Strings.splitIgnoreBlank(regExSql, ",");
                    for (int j = 0; j < strs.length; j++) {
                        // 判断如果路径参数值中含有关键字则返回true,并且结束循环
                        if ("and".equals(strs[j]) || "or".equals(strs[j]) || "into".equals(strs[j])) {
                            if (value.contains(" " + strs[j] + " ")) {
                                isError = true;
                                log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                                break;
                            }
                        } else {
                            if (value.contains(" " + strs[j] + " ")
                                    || value.contains(
                                    strs[j] + " ")) {
                                isError = true;
                                log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                                break;
                            }
                        }
                    }
                    if (isError) {
                        break;
                    }
                }
                if (isError) {
                    break;
                }
            }
        }
        if (xssEnable) {
            if (!isError) {
                // XSS漏洞过滤
                while (values2.hasNext()) {
                    String[] valueArray = (String[]) values2.next();
                    for (int i = 0; i < valueArray.length; i++) {
                        String value = valueArray[i].toLowerCase();
                        String[] strs = Strings.splitIgnoreBlank(regExXss, ",");
                        for (int j = 0; j < strs.length; j++) {
                            if (value.contains("<" + strs[j] + ">")
                                    || value.contains("<" + strs[j])
                                    || value.contains(strs[j] + ">")) {
                                log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "XSS关键字过滤:" + value);
                                isError = true;
                                break;
                            }
                        }
                        if (isError) {
                            break;
                        }
                    }
                    if (isError) {
                        break;
                    }
                }
            }
        }
        return isError;
    }

    private boolean checkParamsJson(ServletRequest req1) {
        RequestWrapper req = (RequestWrapper) req1;
        List<String> parameterValues = getParameterValues(req);
        boolean isError = false;
        String regExSql = "select,update,and,or,delete,insert,trancate,char,chr,into,substr,declare,exec,count,drop,execute";
        String regExSql2 = "extractvalue,updatexml";
        String regExXss = "script,iframe";
        if (sqlEnable) {
            //SQL过滤
            for (String value : parameterValues) {
                value = value.toLowerCase();
                //分拆关键字
                String[] strs = Strings.splitIgnoreBlank(regExSql, ",");
                for (int j = 0; j < strs.length; j++) {
                    // 判断如果路径参数值中含有关键字则返回true,并且结束循环
                    if ("and".equals(strs[j]) || "or".equals(strs[j]) || "into".equals(strs[j])) {
                        if (value.contains(" " + strs[j] + " ")) {
                            isError = true;
                            log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                            break;
                        }
                    } else {
                        if (value.contains(" " + strs[j] + " ")
                                || value.contains(
                                strs[j] + " ")) {
                            isError = true;
                            log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                            break;
                        }
                    }
                }
                if (isError) {
                    break;
                }

                //分拆关键字2
                String[] strs2 = Strings.splitIgnoreBlank(regExSql2, ",");
                for (int j = 0; j < strs2.length; j++) {
                    // 判断如果路径参数值中含有关键字则返回true,并且结束循环
                    if (value.contains(strs2[j])) {
                        isError = true;
                        log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                        break;
                    }
                }
                if (isError) {
                    break;
                }
            }
        }
        if (xssEnable) {
            if (!isError) {
                // XSS漏洞过滤
                for (String value : parameterValues) {
                    value = value.toLowerCase();
                    String[] strs = Strings.splitIgnoreBlank(regExXss, ",");
                    for (int j = 0; j < strs.length; j++) {
                        if (value.contains("<" + strs[j] + ">")
                                || value.contains("<" + strs[j])
                                || value.contains(strs[j] + ">")) {
                            log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "XSS关键字过滤:" + value);
                            isError = true;
                            break;
                        }
                    }
                    if (isError) {
                        break;
                    }
                }
            }
        }
        return isError;
    }

    private List<String> getParameterValues(RequestWrapper request) {
        List<String> list = new ArrayList<>();
        String contentType = request.getContentType();
        if (Strings.isNotBlank(contentType) && contentType.contains("application/json")) {
            // json数据
            try {
                String json = request.getBodyString();
                if (Strings.isNotBlank(json)) {
                    NutMap map = Json.fromJson(NutMap.class, json);
                    for (String key : map.keySet()) {
                        if (Lang.isNotEmpty(map.get(key)) && map.get(key) instanceof String) {
                            list.add(map.getString(key));
                        }
                    }
                }
            } catch (JsonException e) {
                log.errorf("WebFilter getParameterValues err : %s", e.getMessage());
            }
        }
        Iterator<String[]> values = request.getParameterMap().values().iterator();
        while (values.hasNext()) {
            String[] valueArray = values.next();
            for (int i = 0; i < valueArray.length; i++) {
                String value = valueArray[i].toLowerCase();
                list.add(value);
            }
        }
        return list;
    }
}
