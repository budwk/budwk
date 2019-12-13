package com.budwk.nb.web.commons.processor;

import com.budwk.nb.commons.base.Result;
import org.apache.commons.lang3.StringUtils;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SQL XSS拦截
 * Created by wizzer on 2016/7/1.
 * Updated by 黄川 huchuc@vip.qq.com on 2019/12/2.
 */
public class XssSqlFilterProcessor extends AbstractProcessor {

    private static final Log log = Logs.get();
    private PropertiesProxy conf;
    private List<String> ignoreList;

    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        try {
            conf = config.getIoc().get(PropertiesProxy.class, "conf");
            ignoreList = conf.getList("system.xsssql.ignore.urls", ",") == null ? new ArrayList<>() : conf.getList("system.xsssql.ignore.urls", ",");
        } catch (Exception e) {
        }
    }

    public void process(ActionContext ac) throws Throwable {
        if (checkUrl(ac) && checkParams(ac)) {
            NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(Mvcs.getMessage(ac.getRequest(), "system.paramserror")));
            return;
        }
        doNext(ac);
    }

    private boolean checkUrl(ActionContext ac) {
        String path = ac.getPath();
        return !ignoreList.contains(path);
    }

    private boolean checkParams(ActionContext ac) {
        HttpServletRequest req = ac.getRequest();
        Iterator<String[]> values = req.getParameterMap().values().iterator();
        Iterator<String[]> values2 = req.getParameterMap().values().iterator();// 因为是游标所以要重新获取
        boolean isError = false;
        String regExSql = "select,update,and,or,delete,insert,trancate,char,chr,into,substr,declare,exec,count,drop,execute";
        String regExXss = "script,iframe";
        //SQL过滤
        while (values.hasNext()) {
            String[] valueArray = values.next();
            for (int i = 0; i < valueArray.length; i++) {
                String value = valueArray[i].toLowerCase();
                //分拆关键字
                String[] strs = StringUtils.split(regExSql, ",");
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
        if (!isError) {
            // XSS漏洞过滤
            while (values2.hasNext()) {
                String[] valueArray = (String[]) values2.next();
                for (int i = 0; i < valueArray.length; i++) {
                    String value = valueArray[i].toLowerCase();
                    String[] strs = StringUtils.split(regExXss, ",");
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
        return isError;
    }
}
