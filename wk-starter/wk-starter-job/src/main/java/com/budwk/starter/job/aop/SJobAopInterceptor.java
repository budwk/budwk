package com.budwk.starter.job.aop;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.job.JobInfo;
import com.budwk.starter.job.annotation.SJob;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author wizzer@qq.com
 */
@IocBean(singleton = false)
@Slf4j
public class SJobAopInterceptor implements MethodInterceptor {
    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    private RedissonClient redissonClient;
    @Inject
    private PubSubService pubSubService;
    private static final String instanceId = R.UU32();

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        IocBean iocBean = method.getDeclaringClass().getAnnotation(IocBean.class);
        SJob sJob = method.getAnnotation(SJob.class);
        String taskId = "";
        Object[] args = chain.getArgs();
        if (args != null && args.length > 0) {
            taskId = Strings.sNull(args[0]);
        }
        long startTime = System.nanoTime();
        long tookTime = 0L;
        String jobId = R.UU32();
        try {
            RLock rLock = redissonClient.getLock(RedisConstant.JOB_EXECUTE + iocBean.name() + ":" + sJob.value());
            if (rLock.tryLock(3, TimeUnit.SECONDS)) {
                chain.doChain();
                tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                doSendMessage(null, tookTime, sJob.value(), taskId, instanceId, jobId);
                log.info("SJob iocName:{} jobName:{} taskId:{} instanceId:{} jobId:{} - Success", iocBean.name(), sJob.value(), taskId, instanceId, jobId);
            } else {
                log.info("SJob iocName:{} jobName:{} Locked", iocBean.name(), sJob.value());
            }
        } catch (Throwable e) {
            tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            doSendMessage(e, tookTime, sJob.value(), taskId, instanceId, jobId);
            log.error("SJob iocName:{} jobName:{} taskId:{} instanceId:{} jobId:{} - Exception:{}", iocBean.name(),
                    sJob.value(), taskId, instanceId, jobId, e.getMessage());
            throw e;
        }
    }

    protected void doSendMessage(Throwable e, long tookTime, String jobName, String taskId, String instanceId, String jobId) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setTaskId(taskId);
        jobInfo.setJobName(jobName);
        jobInfo.setTookTime(tookTime);
        jobInfo.setEndTime(System.currentTimeMillis());
        jobInfo.setInstanceId(instanceId);
        jobInfo.setJobId(jobId);
        jobInfo.setSuccess(e == null);
        jobInfo.setMessage(e == null ? "" : Strings.cutStr(250, e.getMessage(), "..."));
        pubSubService.fire(RedisConstant.JOB_SUBSCRIBE, Json.toJson(jobInfo, JsonFormat.compact()));
    }
}
