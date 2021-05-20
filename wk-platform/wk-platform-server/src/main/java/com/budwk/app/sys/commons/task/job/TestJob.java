package com.budwk.app.sys.commons.task.job;

import com.budwk.starter.job.annotation.SJob;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer@qq.com
 */
@IocBean(name = "testJob",singleton = false)
@Slf4j
public class TestJob {

    @SJob("demo")
    public void demo(String taskId, String params) {
        log.info("sjob:{} taskId:{} params:{}", "demo", taskId, params);
    }
}
