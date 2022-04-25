package com.budwk.app.sys.commons.task;

import com.budwk.starter.common.constant.RedisConstant;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author wizzer@qq.com
 */
@IocBean
public class TaskJob implements Job {
    @Inject
    private PubSubService pubSubService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        pubSubService.fire(RedisConstant.JOB_PUBLISH, Json.toJson(jobExecutionContext.getJobDetail().getJobDataMap()));
    }
}
