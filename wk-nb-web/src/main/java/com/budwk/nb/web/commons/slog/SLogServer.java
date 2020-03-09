package com.budwk.nb.web.commons.slog;

import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.slog.services.SLogSerivce;
import com.budwk.nb.sys.models.Sys_log;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer(wizzer.cn) on 2016/6/22.
 */
@IocBean
public class SLogServer {

    private static final Log log = Logs.get();
    private static final int STATCK_TRACE_EL_NUMBER = 2;

    @Inject
    protected SLogSerivce sLogSerivce;

    /**
     * 异步插入日志
     *
     * @param syslog 日志对象
     */
    @Async
    public void async(Sys_log syslog) {
        this.sync(syslog);
    }

    /**
     * 同步插入日志
     *
     * @param syslog 日志对象
     */
    public void sync(Sys_log syslog) {
        try {
            sLogSerivce.create(syslog);
        } catch (Throwable e) {
            log.error("create syslog sync fail", e);
        }
    }

    /**
     * 本方法通常由aop拦截器调用.
     *
     * @param t      日志类型
     * @param tag    标签
     * @param source 源码位置
     * @param els    消息模板的EL表达式预处理表
     * @param param  是否异步插入
     * @param result 是否异步插入
     * @param async  是否异步插入
     * @param args   方法参数
     * @param re     方法返回值
     * @param method 方法实例
     * @param obj    被拦截的对象
     * @param e      异常对象
     */
    public void log(String t, String type, String tag, String source, CharSegment seg,
                    Map<String, El> els, boolean param, boolean result,
                    boolean async,
                    Object[] args, Object re, Method method, Object obj,
                    Throwable e) {
        String slogMsg = null;
        if (seg.hasKey()) {
            Context ctx = Lang.context();
            List<String> names = MethodParamNamesScaner.getParamNames(method);
            if (names != null) {
                for (int i = 0; i < names.size() && i < args.length; i++) {
                    ctx.set(names.get(i), args[i]);
                }
            }
            ctx.set("obj", obj);
            ctx.set("args", args);
            ctx.set("re", re);
            ctx.set("return", re);
            ctx.set("req", Mvcs.getReq());
            ctx.set("resp", Mvcs.getResp());
            for (String key : seg.keys()) {
                ctx.set(key, els.get(key).eval(ctx));
            }
            slogMsg = seg.render(ctx).toString();
        } else {
            slogMsg = seg.getOrginalString();
            if (Strings.isBlank(slogMsg)) {
                slogMsg = Strings.sNull(Mvcs.getReq().getAttribute("_slog_msg"));
            }
        }
        String dbParam = "";
        String dbResult = "";
        if (param && args != null) {
            try {
                dbParam = Json.toJson(args);
            } catch (Exception e1) {
                dbParam = "Json Serialization error";
            }
        }
        if (result && re != null) {
            try {
                dbParam = Json.toJson(re);
            } catch (Exception e1) {
                dbParam = "Json Serialization error";
            }
        }
        log(type, tag, source, slogMsg, async, dbParam, dbResult);
    }


    public void log(String type, String tag, String source, String msg, boolean async, String param, String result) {
        Sys_log slog = makeLog(type, tag, source, msg, param, result);
        if (async) {
            async(slog);
        } else {
            sync(slog);
        }
    }

    public static Sys_log makeLog(String type, String tag, String source, String msg, String param, String result) {
        Sys_log sysLog = new Sys_log();
        if (type == null || tag == null) {
            throw new RuntimeException("type/tag can't null");
        }
        if (source == null) {
            StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
            if (tmp.length > STATCK_TRACE_EL_NUMBER) {
                source = tmp[2].getClassName() + "#" + tmp[2].getMethodName();
            } else {
                source = "main";
            }

        }
        sysLog.setId(R.UU32());
        sysLog.setType(type);
        sysLog.setTag(tag);
        sysLog.setSrc(source);
        sysLog.setMsg(msg);
        sysLog.setParam(param);
        sysLog.setResult(result);
        if (Mvcs.getReq() != null) {
            sysLog.setIp(Lang.getIP(Mvcs.getReq()));
            sysLog.setUserAgent(Mvcs.getReq().getHeader("User-Agent"));
        }
        sysLog.setCreatedBy(StringUtil.getPlatformUid());
        sysLog.setCreatedAt(Times.now().getTime());
        sysLog.setUpdatedBy(StringUtil.getPlatformUid());
        sysLog.setUpdatedAt(Times.now().getTime());
        sysLog.setDelFlag(false);
        sysLog.setLoginname(StringUtil.getPlatformLoginname());
        sysLog.setUsername(StringUtil.getPlatformUsername());
        return sysLog;
    }
}