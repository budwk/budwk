package com.budwk.app.device.handler.task;

/**
 * 延迟任务
 * @author zyang
 */
public interface DelayTaskHelper {
    void delayRun(Runnable runnable, long delaySeconds);

    void delayRunAsync(Runnable runnable, long delaySeconds);
}
