package com.budwk.app.device.handler.task;

/**
 *
 * @author zyang
 */
public class TimerTaskEntry implements Comparable<TimerTaskEntry> {

    // 任务列表
    private TimerTaskList list;
    // 后面一个任务
    private TimerTaskEntry next;
    // 前面一个任务
    private TimerTaskEntry prev;

    // 当前的任务
    private TimerTask timerTask;
    // 过期时间，毫秒
    private Long expirationMs;

    public TimerTaskEntry(TimerTask timerTask, Long expirationMs) {
        this.timerTask = timerTask;
        if(null!=timerTask) {
            timerTask.setTimerTaskEntry(this);
        }
        this.expirationMs = expirationMs;
    }
    
    // 是否已取消 
    public boolean cancelled() {
        return null == timerTask || this != timerTask.getTimerTaskEntry();
    }

    // 移除任务
    public void remove() {
        TimerTaskList current = list;
        while (null != current) {
            current.remove(this);
            current = list;
        }
    }

    @Override
    public int compareTo(TimerTaskEntry o) {
        return Long.compare(this.expirationMs, o.getExpirationMs());
    }

    public TimerTaskList getList() {
        return list;
    }

    public void setList(TimerTaskList list) {
        this.list = list;
    }

    public TimerTaskEntry getNext() {
        return next;
    }

    public void setNext(TimerTaskEntry next) {
        this.next = next;
    }

    public TimerTaskEntry getPrev() {
        return prev;
    }

    public void setPrev(TimerTaskEntry prev) {
        this.prev = prev;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public Long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(Long expirationMs) {
        this.expirationMs = expirationMs;
    }
    

}
