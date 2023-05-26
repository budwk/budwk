package com.budwk.app.device.handler.task;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author zyang
 */
@Slf4j
public class TimerThread extends Thread {

    public static TimerThread daemon(final String name, Runnable runnable) {
        return new TimerThread(name, runnable, true);
    }

    public static TimerThread nonDaemon(final String name, Runnable runnable) {
        return new TimerThread(name, runnable, false);
    }

    public TimerThread(final String name, boolean daemon) {
        super(name);
        configureThread(name, daemon);
    }

    public TimerThread(final String name, Runnable runnable, boolean daemon) {
        super(runnable, name);
        configureThread(name, daemon);
    }

    private void configureThread(final String name, boolean daemon) {
        setDaemon(daemon);
        setUncaughtExceptionHandler((t, e) -> {
            log.error("Uncaught exception in thread {}", name,e);
        });
    }
}
