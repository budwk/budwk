package com.budwk.app.device.handler.task;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zyang
 */
public class SystemTimer implements Timer {

    private String executorName;
    private long tickMs;
    private int wheelSize;
    private long startMs;
    private AtomicInteger taskCounter = new AtomicInteger(0);

    private TimingWheel timingWheel;

    private ExecutorService taskExecutor;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();

    public SystemTimer() {
        this("SystemTimer", 1, 20, System.currentTimeMillis());
    }

    public SystemTimer(String executorName, long tickMs, int wheelSize, long startMs) {
        this.executorName = executorName;
        this.tickMs = tickMs;
        this.wheelSize = wheelSize;
        this.startMs = startMs;
        this.init();
    }

    private void init() {

        int thread = Runtime.getRuntime().availableProcessors();
        taskExecutor = Executors.newFixedThreadPool(thread, r -> {
            return TimerThread.nonDaemon("executor-" + executorName, r);
        });
        timingWheel = new TimingWheel(tickMs, wheelSize, startMs, taskCounter, delayQueue);
    }

    @Override
    public void add(TimerTask timerTask) {
        readLock.lock();
        try {
            addTimerTaskEntry(new TimerTaskEntry(timerTask, timerTask.getDelayMs() + System.currentTimeMillis()));
        } finally {
             readLock.unlock();
        }
    }

    private void addTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
        if (!timingWheel.add(timerTaskEntry)) {
            // 如果已经过期且没有取消
            if (!timerTaskEntry.cancelled()) {
                taskExecutor.submit(timerTaskEntry.getTimerTask());
            }
        }
    }

    @Override
    public boolean advanceClock(long timeoutMs) {
        boolean lock = false;
        try {
            TimerTaskList bucket = delayQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
            if (null != bucket) {
                writeLock.lock();
                lock = true;
                while (null != bucket) {
                    timingWheel.advanceClock(bucket.getExpiration());
                    bucket.flush(this::addTimerTaskEntry);
                    bucket = delayQueue.poll();
                }
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SystemTimer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (lock) {
                writeLock.unlock();
            }
        }
    }

    @Override
    public int getSize() {
        return taskCounter.get();
    }

    @Override
    public void shutdown() {
        taskExecutor.shutdown();
    }

}
