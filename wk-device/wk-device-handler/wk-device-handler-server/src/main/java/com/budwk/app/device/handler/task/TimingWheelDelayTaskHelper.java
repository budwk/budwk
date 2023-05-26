package com.budwk.app.device.handler.task;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@IocBean(create = "init", name = "timingWheel", depose = "stop")
public class TimingWheelDelayTaskHelper implements DelayTaskHelper {

    @Inject
    private PropertiesProxy conf;
    private SystemTimer timer;

    private ScheduledExecutorService ses;

    public void init() {
        long tickMs = conf.getLong("delay.tickMs", 1000L);
        int wheelSize = conf.getInt("delay.wheelSize", 20);
        timer = new SystemTimer("handler-delayTask", tickMs, wheelSize, System.currentTimeMillis());
        // 使用一个定时器，按照时间轮的间隔每次拨动时间轮往前走一格
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> timer.advanceClock(tickMs), tickMs, tickMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (null != timer) {
            timer.shutdown();
        }
    }

    @Override
    public void delayRun(Runnable runnable, long delaySeconds) {
        if (null == timer) {
            init();
        }
        timer.add(new TimerTask(delaySeconds * 1000L, runnable));
    }

    @Override
    public void delayRunAsync(Runnable runnable, long delaySeconds) {
        CompletableFuture.runAsync(() -> delayRun(runnable, delaySeconds));
    }
}
