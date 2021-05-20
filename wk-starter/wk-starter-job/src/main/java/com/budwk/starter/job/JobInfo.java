package com.budwk.starter.job;

import lombok.Data;

/**
 * @author wizzer@qq.com
 */
@Data
public class JobInfo {
    /**
     * ioc对象名称
     */
    private String iocName;
    /**
     * 方法名称
     */
    private String jobName;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 执行实例ID
     */
    private String instanceId;

    /**
     * 执行ID
     */
    private String jobId;

    /**
     * 传递参数
     */
    private String params;

    /**
     * 执行耗时
     */
    private long tookTime;

    /**
     * 结束时间
     */
    private long endTime;

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 错误消息
     */
    private String message;

}
