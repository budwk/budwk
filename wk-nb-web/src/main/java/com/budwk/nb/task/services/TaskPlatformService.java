package com.budwk.nb.task.services;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2018/3/19.
 */
public interface TaskPlatformService {
    /**
     * 判断任务是否存在
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    boolean isExist(String jobName, String jobGroup);


    /**
     * 添加新任务
     *
     * @param jobName   任务标识
     * @param jobGroup  任务组
     * @param className 类名
     * @param cron      表达式
     * @param comment   任务备注
     * @param dataMap   传递参数
     * @throws Exception
     */
    void add(String jobName, String jobGroup, String className, String cron, String comment, String dataMap) throws Exception;

    /**
     * 删除任务
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    boolean delete(String jobName, String jobGroup);

    /**
     * 清除所有任务
     */
    void clear();

    /**
     * 获取cron表达式最近执行时间
     * @param cronExpression 表达式
     * @return
     * @throws Exception
     */
    List<String> getCronExeTimes(String cronExpression) throws Exception;
}
