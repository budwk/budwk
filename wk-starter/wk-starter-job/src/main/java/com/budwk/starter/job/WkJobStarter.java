package com.budwk.starter.job;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.job.annotation.SJob;
import lombok.extern.slf4j.Slf4j;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.resource.Scans;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
@Slf4j
public class WkJobStarter implements PubSub {
    @Inject
    protected PubSubService pubSubService;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected static final String PRE = "job.";

    @PropDoc(value = "Database 扫描的包名", defaultValue = "")
    public static final String PROP_TASK_PACKAGE = PRE + "package";

    @Inject
    protected PropertiesProxy conf;

    public void init() {
        List<String> packages = conf.getList(PROP_TASK_PACKAGE);
        packages.forEach(pkg -> {
            for (Class<?> klass : Scans.me().scanPackage(pkg)) {
                for (Method method : klass.getDeclaredMethods()) {
                    SJob sJob = method.getAnnotation(SJob.class);
                    if (sJob != null) {
                        pubSubService.reg(RedisConstant.JOB_PUBLISH + ":" + sJob.value(), this);
                    }
                }
            }
        });
    }

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.startsWith(RedisConstant.JOB_PUBLISH)) {
            return;
        }
        JobInfo wkJobInfo = Json.fromJson(JobInfo.class, message);
        try {
            Class<?> iocClass = ioc.getType(wkJobInfo.getIocName());
            // ioc 名称对得上 且带SJob注解
            if (iocClass != null) {
                for (Method method : iocClass.getDeclaredMethods()) {
                    SJob sJob = method.getAnnotation(SJob.class);
                    if (sJob != null && sJob.value().equalsIgnoreCase(wkJobInfo.getJobName())) {
                        method.invoke(ioc.get(iocClass), wkJobInfo.getTaskId(), wkJobInfo.getParams());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
