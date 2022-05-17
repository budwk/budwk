package com.budwk.app.sys.commons.task;

import com.budwk.app.sys.models.Sys_task_history;
import com.budwk.app.sys.services.SysTaskHistoryService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.job.JobInfo;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class TaskHistory implements PubSub {
    @Inject
    private PubSubService pubSubService;
    @Inject
    private RedisService redisService;
    @Inject
    private SysTaskHistoryService sysTaskHistoryService;

    public void init() {
        pubSubService.reg(RedisConstant.JOB_SUBSCRIBE, this);
    }

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equalsIgnoreCase(RedisConstant.JOB_SUBSCRIBE)) {
            return;
        }
        JobInfo jobInfo = Json.fromJson(JobInfo.class, message);
        Sys_task_history history = new Sys_task_history();
        history.setTaskId(jobInfo.getTaskId());
        history.setMessage(Strings.sNull(history.getMessage()));
        history.setSuccess(jobInfo.isSuccess());
        history.setInstanceId(jobInfo.getInstanceId());
        history.setJobId(jobInfo.getJobId());
        history.setEndTime(jobInfo.getEndTime());
        history.setTookTime(jobInfo.getTookTime());
        // 多实例会收到重复的多条,只需插入一条即可
        String uuid = R.UU32();
        String redisVal = Strings.sNull(redisService.get(RedisConstant.JOB_HISTORY + history.getInstanceId() + ":" + history.getJobId()));
        if (Strings.isBlank(redisVal)) {
            redisService.setex(RedisConstant.JOB_HISTORY + history.getInstanceId() + ":" + history.getJobId(), 3, uuid);
        }
        redisVal = Strings.sNull(redisService.get(RedisConstant.JOB_HISTORY + history.getInstanceId() + ":" + history.getJobId()));
        if (redisVal.equalsIgnoreCase(uuid)) {
            sysTaskHistoryService.insert(history);
        }
    }
}
