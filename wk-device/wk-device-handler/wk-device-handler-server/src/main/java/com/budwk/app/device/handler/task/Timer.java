package com.budwk.app.device.handler.task;

/**
 *
 * @author zyang
 */
public interface Timer {

    /**
     * 添加新任务
     *
     * @param timerTask
     */
    void add(TimerTask timerTask);

    /**
     * 提前内部时钟，并执行已过期的任务
     *
     * @param timeoutMs
     * @return
     */
    boolean advanceClock(long timeoutMs);

    /**
     * 获取待执行任务数量
     *
     * @return
     */
    int getSize();

    /**
     * 停止
     */
    void shutdown();
}


