package com.budwk.app.sys.commons.task;

import com.budwk.starter.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Times;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author wizzer@qq.com
 */
@IocBean(singleton = false)
@Slf4j
public class TaskJob implements Job {
    @Inject
    private PubSubService pubSubService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        log.debug("[TaskJob] 执行任务：{}，执行时间 {}", jobDataMap.getString("jobName"), Times.getNowSDT());
        pubSubService.fire(RedisConstant.JOB_PUBLISH + ":" + jobDataMap.getString("jobName"), Json.toJson(jobExecutionContext.getJobDetail().getJobDataMap()));
    }
}
