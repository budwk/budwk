package com.budwk.nb.task.commons.core;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.sys.models.Sys_task;
import com.budwk.nb.sys.services.SysTaskService;
import com.budwk.nb.task.commons.base.Globals;
import com.budwk.nb.task.services.TaskPlatformService;
import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Modules;
import org.quartz.Scheduler;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2018/3/19.
 */
@IocBean(create = "init", depose = "depose")
@Modules(packages = "com.budwk.nb")
public class TaskMainLauncher {
    private static final Log log = Logs.get();
    @Inject
    private TaskPlatformService taskPlatformService;
    @Inject
    @Reference
    private SysTaskService sysTaskService;
    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private Dao dao;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        nb.run();
    }

    public void init() {
        if (!dao.exists("sys_qrtz_triggers") && !dao.exists("SYS_QRTZ_TRIGGERS")) {
            //执行Quartz SQL脚本
            String dbType = dao.getJdbcExpert().getDatabaseType();
            log.debug("dbType:::" + dbType);
            FileSqlManager fmq = new FileSqlManager("quartz/" + dbType.toLowerCase() + ".sql");
            List<Sql> sqlListq = fmq.createCombo(fmq.keys());
            Sql[] sqlsq = sqlListq.toArray(new Sql[sqlListq.size()]);
            for (Sql sql : sqlsq) {
                dao.execute(sql);
            }
        }
        if (0 == sysTaskService.count()) {
            //定时任务示例
            Sys_task task = new Sys_task();
            task.setDisabled(true);
            task.setName("测试任务");
            task.setJobClass("com.budwk.nb.task.commons.ext.job.TestJob");
            task.setCron("*/5 * * * * ?");
            task.setData("{\"hi\":\"微信号:Wizzer QQ:11624317 | send red packets of support,thank u\"}");
            task.setNote("微信号:Wizzer QQ:11624317 | 欢迎发送红包以示支持，多谢。。");
            sysTaskService.insert(task);
        }
        ioc.get(Globals.class).init(sysTaskService);
    }

    public void depose() {
        // 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
        try {
            Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
        } catch (Throwable e) {
        }
        // 解决quartz有时候无法停止的问题
        try {
            ioc.get(Scheduler.class).shutdown(true);
        } catch (Exception e) {
        }
        // 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
        // 多webapp共享mysql驱动的话,以下语句删掉
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                log.debug("deregisterDriver: " + className);
                DriverManager.deregisterDriver(driver);
            } catch (Exception e) {
            }
        }
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("com.alibaba.druid:type=MockDriver");
            if (mbeanServer.isRegistered(objectName)) {
                mbeanServer.unregisterMBean(objectName);
            }
            objectName = new ObjectName("com.alibaba.druid:type=DruidDriver");
            if (mbeanServer.isRegistered(objectName)) {
                mbeanServer.unregisterMBean(objectName);
            }
        } catch (Exception ex) {
        }
    }
}
