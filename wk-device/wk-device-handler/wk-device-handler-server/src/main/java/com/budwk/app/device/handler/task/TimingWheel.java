package com.budwk.app.device.handler.task;


import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zyang
 */
public class TimingWheel {

    // 每个格子的毫秒数
    private final Long tickMs;
    // 格子数量
    private final int wheelSize;
    // 开始的还描述
    private final Long startMs;
    //
    private final AtomicInteger taskCounter;
    private final DelayQueue<TimerTaskList> queue;

    private long interval;
    private long currentTime;

    private volatile TimingWheel overflowWheel = null;

    private TimerTaskList[] buckets = null;

    public TimingWheel(long tickMsLong, int wheelSize, long startMs, AtomicInteger taskCounter, DelayQueue<TimerTaskList> queue) {
        this.tickMs = tickMsLong;
        this.wheelSize = wheelSize;
        this.startMs = startMs;
        this.taskCounter = taskCounter;
        this.queue = queue;
        this.init();
    }

    private void init() {
        interval = tickMs * wheelSize;
        currentTime = startMs - (startMs % tickMs);
        buckets = new TimerTaskList[wheelSize];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new TimerTaskList(taskCounter);
        }
    }

    private void addOverflowWheel() {
        synchronized (this) {
            if (null == overflowWheel) {
                overflowWheel = new TimingWheel(interval, wheelSize, currentTime, taskCounter, queue);
            }
        }
    }

    public boolean add(TimerTaskEntry timerTaskEntry) {
        if (null == timerTaskEntry) {
            return false;
        }
        long expiration = timerTaskEntry.getExpirationMs();
        if (timerTaskEntry.cancelled()) {
            return false;
        }
        if (expiration < currentTime + tickMs) {
            // 已经过期了
            return false;
        }
        if (expiration < currentTime + interval) {
            // 计算出位置
            long virtualId = expiration / tickMs;
            // 获取存放的任务列表
            TimerTaskList bucket = buckets[Long.valueOf(virtualId % wheelSize).intValue()];
            bucket.add(timerTaskEntry);
            // 设置过期时间
            if (bucket.setExpiration(virtualId * tickMs)) {
                // 如果是这轮要执行的，那么就加入到延迟队列中去
                queue.offer(bucket);
            }
            return true;
        }
        // 过期时间超出了一轮，那么放到外层的时间轮去
        if (null == overflowWheel) {
            addOverflowWheel();
        }
        return overflowWheel.add(timerTaskEntry);
    }

    /**
     * 将时间提前
     *
     * @param timeMs
     */
    public void advanceClock(long timeMs) {
        if (timeMs >= currentTime + tickMs) {
            currentTime = timeMs - (timeMs % tickMs);
            // 如果存在外层，也调整一下
            if (null != overflowWheel) {
                overflowWheel.advanceClock(currentTime);
            }
        }
    }
}
