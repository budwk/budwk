package com.budwk.starter.log.aop;

import com.budwk.starter.log.LogService;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.log.enums.LogType;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.el.El;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@IocBean(singleton = false)
@Slf4j
public class SLogAopInterceptor implements MethodInterceptor {

    protected String source;
    protected LogType logType;
    protected String tag;
    protected CharSegment segment;
    protected Map<String, El> els;
    @Inject("refer:$ioc")
    protected Ioc ioc;
    protected LogService logService;
    protected boolean param;
    protected boolean result;

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        SLog slog = method.getAnnotation(SLog.class);
        els = new HashMap<>();
        segment = new CharSegment(slog.value());
        for (String key : segment.keys()) {
            els.put(key, new El(key));
        }
        this.source = method.getDeclaringClass().getName() + "#" + method.getName();
        this.tag = slog.tag();
        this.param = slog.param();
        this.result = slog.result();
        this.logType = slog.type();
        SLog _s = method.getDeclaringClass().getAnnotation(SLog.class);
        if (_s != null) {
            this.tag = Strings.isBlank(this.tag) ? _s.tag() : _s.tag() + ":" + this.tag;
        }
        long startTime = System.nanoTime();
        long tookTime = 0L;
        try {
            chain.doChain();
            tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            doLog(segment, chain, null, tookTime);
        } catch (Throwable e) {
            tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            doLog(segment, chain, e, tookTime);
            throw e;
        }
    }

    protected void doLog(CharSegment seg, InterceptorChain chain, Throwable e, long tookTime) {
        if (logService == null)
            logService = ioc.get(LogService.class);
        try {
            logService.log(logType,
                    tag,
                    source,
                    seg,
                    els,
                    param,
                    result,
                    chain.getArgs(),
                    chain.getReturn(),
                    chain.getCallingMethod(),
                    chain.getCallingObj(),
                    e, tookTime);
        } catch (Exception e1) {
            log.error("SLog日志异常", e1);
        }
    }
}
