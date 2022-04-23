package com.budwk.app.sys.commons.task;

import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.job.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.integration.quartz.QuartzJob;
import org.nutz.integration.quartz.QuartzManager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.quartz.JobKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时任务服务类
 *
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
@Slf4j
public class TaskServer {
    @Inject
    private QuartzManager quartzManager;
    // 注入下历史记录类,实现消息订阅
    @Inject
    private TaskHistory taskHistory;
    @Inject
    private PubSubService pubSubService;

    public void init() {
        quartzManager.clear();
    }

    /**
     * 执行任务
     *
     * @param taskId  任务ID
     * @param iocName IOC对象名
     * @param jobName 执行方法名
     * @param params  传递参数
     */
    public void doNow(String taskId, String iocName, String jobName, String params) {
        JobInfo info = new JobInfo();
        info.setTaskId(taskId);
        info.setIocName(iocName);
        info.setJobName(jobName);
        info.setParams(params);
        pubSubService.fire(RedisConstant.JOB_PUBLISH + ":" + jobName, Json.toJson(info));
    }

    /**
     * 判断任务是否存在
     *
     * @param taskId 任务ID
     * @return
     */
    public boolean isExist(String taskId) {
        return quartzManager.exist(new JobKey(taskId, taskId));
    }

    /**
     * 添加定时任务
     *
     * @param taskId  任务ID
     * @param iocName IOC对象名称
     * @param jobName 任务作业名称
     * @param cron    表达式
     * @param note    备注信息
     * @param params  传递参数
     * @throws Exception
     */
    public void add(String taskId, String iocName, String jobName, String cron, String note, String params) throws Exception {
        QuartzJob qj = new QuartzJob();
        qj.setJobName(taskId);
        qj.setJobGroup(taskId);
        qj.setClassName("com.budwk.app.sys.commons.task.TaskJob");
        qj.setCron(cron);
        qj.setComment(note);
        JobInfo info = new JobInfo();
        info.setTaskId(taskId);
        info.setIocName(iocName);
        info.setJobName(jobName);
        info.setParams(params);
        qj.setDataMap(Json.toJson(info));
        quartzManager.add(qj);
    }

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return
     */
    public boolean delete(String taskId) {
        QuartzJob qj = new QuartzJob();
        qj.setJobName(taskId);
        qj.setJobGroup(taskId);
        return quartzManager.delete(qj);
    }

    /**
     * 清除任务
     */
    public void clear() {
        quartzManager.clear();
    }

    /**
     * 获取cron表达式最近执行时间
     *
     * @param cronExpression 表达式
     * @return
     */
    public List<String> getCronExeTimes(String cronExpression) throws Exception {
        List<String> list = new ArrayList<>();
        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        cronTriggerImpl.setCronExpression(cronExpression);
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(2, 1);
        List<Date> dates = TriggerUtils.computeFireTimesBetween(cronTriggerImpl, null, now, calendar.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < dates.size() && i <= 4; ++i) {
            list.add(dateFormat.format((Date) dates.get(i)));
        }
        return list;
    }
}
