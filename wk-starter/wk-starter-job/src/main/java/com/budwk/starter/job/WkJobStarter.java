package com.budwk.starter.job;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.job.annotation.SJob;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;

import java.lang.reflect.Method;

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

    @Inject
    protected PropertiesProxy conf;

    public void init() {
        pubSubService.reg(RedisConstant.JOB_PUBLISH, this);
    }

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equalsIgnoreCase(RedisConstant.JOB_PUBLISH)) {
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
