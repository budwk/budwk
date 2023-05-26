package com.budwk.app.device.handler.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author zyang
 */
public class TimerTaskList implements Delayed {

    // 任务计数器
    private AtomicInteger taskCounter;

    private TimerTaskEntry root;
    // 过期时间
    private AtomicLong expiration = new AtomicLong(-1);

    public TimerTaskList(AtomicInteger taskCounter) {
        this.taskCounter = taskCounter;
        this.root = new TimerTaskEntry(null, -1L);
        this.root.setNext(root);
        this.root.setPrev(root);
    }

    public void add(TimerTaskEntry timerTaskEntry) {
        boolean done = false;
        while (!done) {
            // 可能这个任务被加入过其他的列表了，在这里删除一下
            timerTaskEntry.remove();
            synchronized (this) {
                // 说明这个任务不在其他任何列表中了
                if (null == timerTaskEntry.getList()) {
                    // 这地方就是调整位置
                    TimerTaskEntry tail = root.getPrev();
                    timerTaskEntry.setNext(root);
                    timerTaskEntry.setPrev(tail);
                    timerTaskEntry.setList(this);
                    root.setPrev(timerTaskEntry);
                    tail.setNext(timerTaskEntry);
                    taskCounter.incrementAndGet();
                    done = true;
                }
            }
        }
    }

    public void remove(TimerTaskEntry timerTaskEntry) {

        synchronized (this) {
            // 两个是同一个对象才说明这个任务确实是在我名下的
            if (this == timerTaskEntry.getList()) {
                timerTaskEntry.getNext().setPrev(timerTaskEntry.getPrev());
                timerTaskEntry.getPrev().setNext(timerTaskEntry.getNext());
                timerTaskEntry.setNext(null);
                timerTaskEntry.setPrev(null);
                timerTaskEntry.setList(null);
                taskCounter.decrementAndGet();
            }
        }
    }

    /**
     * 移除所有任务，并且执行传入的函数
     *
     * @param f
     */
    public void flush(Consumer<TimerTaskEntry> f) {

        synchronized (this) {
            for (TimerTaskEntry timerTaskEntry = root.getNext();
                 timerTaskEntry != root;
                 timerTaskEntry = root.getNext()) {
                remove(timerTaskEntry);
                f.accept(timerTaskEntry);
            }
            expiration.set(-1);
        }
    }

    public long getExpiration() {
        return expiration.get();
    }

    public boolean setExpiration(long expirationMs) {
        return this.expiration.getAndSet(expirationMs) != expirationMs;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(
                Math.max(getExpiration() - System.currentTimeMillis(), 0),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        TimerTaskList that = (TimerTaskList) o;
        return Long.compare(getExpiration(), that.getExpiration());
    }

}
