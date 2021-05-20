package com.budwk.app.sys;

import com.budwk.app.sys.commons.task.TaskServer;
import com.budwk.app.sys.enums.SysConfigType;
import com.budwk.app.sys.enums.SysUnitType;
import com.budwk.app.sys.models.*;
import com.budwk.app.sys.services.SysTaskService;
import com.budwk.app.sys.services.impl.SysTaskServiceImpl;
import com.budwk.starter.common.constant.GlobalConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.boot.NbApp;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.quartz.Scheduler;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init", depose = "depose")
@AdaptBy(type = JsonAdaptor.class)
@Slf4j
public class WkPlatformLauncher {

    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;
    @Inject
    private Dao dao;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk");
        nb.run();
    }

    public void init() {
        //初始化系统表数据
        databaseInit();
        //定时任务初始化
        taskInit();
    }

    public void taskInit() {
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
        SysTaskService sysTaskService = ioc.get(SysTaskServiceImpl.class);
        if (0 == sysTaskService.count()) {
            //定时任务示例
            Sys_task task = new Sys_task();
            task.setDisabled(true);
            task.setName("测试任务");
            task.setCron("*/15 * * * * ?");
            task.setNote("");
            task.setParams("微信号:Wizzer QQ:11624317 | 欢迎发送红包以示支持，多谢。。");
            task.setIocName("testJob");
            task.setJobName("demo");
            sysTaskService.insert(task);
        }
        for (Sys_task task : sysTaskService.query(Cnd.where("disabled", "=", false))) {
            try {
                ioc.get(TaskServer.class).add(task.getId(), task.getIocName(), task.getJobName(), task.getCron(),
                        task.getNote(), task.getParams()
                );
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Async
    private void databaseInit() {
        try {
            if (0 == dao.count(Sys_user.class)) {
                log.info("开始初始化数据库...");
                //初始化配置表
                Sys_config conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_PLATFORM_APPID);
                conf.setType(SysConfigType.TEXT);
                conf.setConfigKey("AppName");
                conf.setConfigValue("欧圣达一体化平台");
                conf.setNote("系统名称");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_PLATFORM_APPID);
                conf.setType(SysConfigType.TEXT);
                conf.setConfigKey("AppShrotName");
                conf.setConfigValue("欧圣达");
                conf.setNote("系统短名称");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                conf.setType(SysConfigType.TEXT);
                conf.setConfigKey("AppDomain");
                conf.setConfigValue("http://127.0.0.1:8888");
                conf.setNote("系统域名");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                conf.setType(SysConfigType.TEXT);
                conf.setConfigKey("AppFileDomain");
                conf.setConfigValue("http://127.0.0.1:9999");
                conf.setNote("文件访问域名");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                conf.setType(SysConfigType.TEXT);
                conf.setConfigKey("AppUploadBase");
                conf.setConfigValue("/upload");
                conf.setNote("文件访问路径");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                conf.setType(SysConfigType.BOOL);
                conf.setConfigKey("AppWebSocket");
                conf.setConfigValue("true");
                conf.setNote("启用WebSocket");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                conf.setType(SysConfigType.BOOL);
                conf.setConfigKey("AppDemoEnv");
                conf.setConfigValue("false");
                conf.setNote("是否演示环境");
                dao.insert(conf);
                conf = new Sys_config();
                conf.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                conf.setType(SysConfigType.BOOL);
                conf.setConfigKey("AppPwdCheck");
                conf.setConfigValue("true");
                conf.setNote("是否启用弱密码检查");
                dao.insert(conf);

                Sys_app sysApp = new Sys_app();
                sysApp.setName("系统公用");
                sysApp.setId(GlobalConstant.DEFAULT_COMMON_APPID);
                sysApp.setDisabled(false);
                sysApp.setLocation(1);
                dao.insert(sysApp);
                sysApp = new Sys_app();
                sysApp.setName("控制中心");
                sysApp.setId(GlobalConstant.DEFAULT_PLATFORM_APPID);
                sysApp.setDisabled(false);
                sysApp.setLocation(2);
                dao.insert(sysApp);

                //初始化单位
                Sys_unit headUnit = new Sys_unit();
                headUnit.setPath("0001");
                headUnit.setName("萌发开源");
                headUnit.setAliasName("BudWK");
                headUnit.setLocation(0);
                headUnit.setAddress("银河-太阳系-地球");
                headUnit.setEmail("");
                headUnit.setTelephone("");
                headUnit.setHasChildren(true);
                headUnit.setParentId("");
                headUnit.setWebsite("https://budwk.com");
                headUnit.setType(SysUnitType.GROUP);
                dao.insert(headUnit);
                Sys_unit unit = new Sys_unit();
                unit.setPath("00010001");
                unit.setName("信息部");
                unit.setAliasName("IT");
                unit.setLocation(1);
                unit.setAddress("银河-太阳系-地球");
                unit.setEmail("wizzer@qq.com");
                unit.setTelephone("");
                unit.setHasChildren(false);
                unit.setParentId("0001");
                unit.setWebsite("https://budwk.com");
                unit.setType(SysUnitType.UNIT);
                dao.insert(unit);

                //初始化角色分组
                Sys_group group = new Sys_group();
                group.setId("SYSTEM");
                group.setName("系统管理组");
                group.setUnitId(headUnit.getId());
                group.setUnitPath(headUnit.getPath());
                dao.insert(group);

                //初始化角色
                Sys_role publicRole = new Sys_role();
                publicRole.setName("公共角色");
                publicRole.setCode("public");
                publicRole.setNote("所有用户默认分配");
                publicRole.setDisabled(false);
                publicRole.setUnitId(headUnit.getId());
                publicRole.setGroupId(group.getId());
                dao.insert(publicRole);
                Sys_role role = new Sys_role();
                role.setName("超级管理员");
                role.setCode(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE);
                role.setNote("超级管理员角色");
                role.setDisabled(false);
                role.setUnitId(headUnit.getId());
                role.setGroupId(group.getId());
                dao.insert(role);

                //初始化用户
                Sys_user user = new Sys_user();
                user.setId("5f8cebd7022c409a94e90da1d840b8bb");
                user.setSerialNo("0");
                user.setLoginname(GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME);
                user.setUsername("超级管理员");
                user.setSalt("r5tdr01s7uglfokpsdmtu15602");
                user.setPassword("1bba9287ebc50b766bff84273d11ccefaa7a8da95d078960f05f116e9d970fb0");
                user.setLoginIp("127.0.0.1");
                user.setLoginAt(0L);
                user.setLoginCount(0);
                user.setNeedChangePwd(false);
                user.setEmail("wizzer@qq.com");
                user.setUnitId(unit.getId());
                user.setUnitPath(unit.getPath());
                user.setCompanyId(headUnit.getId());
                dao.insert(user);

                //不同的插入数据方式(安全)
                dao.insert("sys_role_app", Chain.make("id", R.UU32()).add("appId", GlobalConstant.DEFAULT_COMMON_APPID).add("roleId", role.getId()));
                dao.insert("sys_role_app", Chain.make("id", R.UU32()).add("appId", GlobalConstant.DEFAULT_PLATFORM_APPID).add("roleId", role.getId()));
                dao.insert("sys_role_app", Chain.make("id", R.UU32()).add("appId", GlobalConstant.DEFAULT_COMMON_APPID).add("roleId", publicRole.getId()));
                dao.insert("sys_unit_user", Chain.make("id", R.UU32()).add("userId", user.getId()).add("unitId", unit.getId()));
                dao.insert("sys_role_user", Chain.make("id", R.UU32()).add("userId", user.getId()).add("roleId", role.getId()));
                dao.insert("sys_role_user", Chain.make("id", R.UU32()).add("userId", user.getId()).add("roleId", publicRole.getId()));
                //执行SQL脚本
                FileSqlManager fm = new FileSqlManager("db/");
                fm.setByRow(true);
                List<Sql> sqlList = fm.createCombo(fm.keys());
                Sql[] sqls = sqlList.toArray(new Sql[sqlList.size()]);
                for (Sql sql : sqls) {
                    dao.execute(sql);
                }
                //菜单关联到角色
                List<Sys_menu> list = dao.query(Sys_menu.class, Cnd.where("appId", "=", GlobalConstant.DEFAULT_PLATFORM_APPID));
                for (Sys_menu menu : list) {
                    Sys_role_menu sysRoleMenu = new Sys_role_menu();
                    sysRoleMenu.setRoleId(role.getId());
                    sysRoleMenu.setAppId(GlobalConstant.DEFAULT_PLATFORM_APPID);
                    sysRoleMenu.setMenuId(menu.getId());
                    dao.insert(sysRoleMenu);
                }
                List<Sys_menu> cpmMenulist = dao.query(Sys_menu.class, Cnd.where("appId", "=", GlobalConstant.DEFAULT_COMMON_APPID));
                for (Sys_menu menu : cpmMenulist) {
                    //超级管理员角色
                    Sys_role_menu sysRoleMenu = new Sys_role_menu();
                    sysRoleMenu.setRoleId(role.getId());
                    sysRoleMenu.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                    sysRoleMenu.setMenuId(menu.getId());
                    dao.insert(sysRoleMenu);
                    //公共角色
                    sysRoleMenu = new Sys_role_menu();
                    sysRoleMenu.setRoleId(publicRole.getId());
                    sysRoleMenu.setAppId(GlobalConstant.DEFAULT_COMMON_APPID);
                    sysRoleMenu.setMenuId(menu.getId());
                    dao.insert(sysRoleMenu);
                }
                log.info("初始化数据库结束...");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
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
