package com.budwk.app.device.handler.task;

/**
 *
 * @author zyang
 */
public class TimerTask implements Runnable {

    private Long delayMs;

    private TimerTaskEntry timerTaskEntry;

    private Runnable task;

    public TimerTask(Long delayMs, Runnable task) {
        this.delayMs = delayMs;
        this.task = task;
    }

    public void cancel() {
        synchronized (this) {
            if (null != timerTaskEntry) {
                timerTaskEntry.remove();
                timerTaskEntry = null;
            }
        }
    }

    @Override
    public void run() {
        if (null != task) {
            task.run();
        }
    }

    public TimerTaskEntry getTimerTaskEntry() {
        return timerTaskEntry;
    }

    public void setTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
        this.timerTaskEntry = timerTaskEntry;
    }

    public Long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(Long delayMs) {
        this.delayMs = delayMs;
    }

}
