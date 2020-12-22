package com.budwk.nb;

import com.budwk.nb.cms.models.Cms_site;
import com.budwk.nb.base.constant.PlatformConstant;
import com.budwk.nb.sys.models.*;
import com.budwk.nb.sys.services.SysTaskService;
import com.budwk.nb.task.services.TaskPlatformService;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.ext.i18n.WkLocalizationManager;
import com.budwk.nb.web.commons.ext.pubsub.WebPubSub;
import org.nutz.boot.NbApp;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.shiro.ShiroSessionProvider;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.quartz.Scheduler;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import java.lang.management.ManagementFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2018/3/16.
 */
@IocBean(create = "init", depose = "depose")
@Modules(packages = "com.budwk.nb")
@Localization(value = "locales/", defaultLocalizationKey = "zh_CN")
@Encoding(input = "UTF-8", output = "UTF-8")
@ChainBy(args = "chain/budwk-mvc-chain.json")
@SessionBy(ShiroSessionProvider.class)
public class MainLauncher {
    private static final Log log = Logs.get();
    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;
    @Inject
    private JedisAgent jedisAgent;
    @Inject
    private TaskPlatformService taskPlatformService;
    @Inject
    private SysTaskService sysTaskService;
    @Inject
    private Dao dao;
    /**
     * 注入一下为了进行初始化
     */
    @Inject
    private WebPubSub webPubSub;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        nb.run();
    }

    public static NbApp warMain(ServletContext sc) {
        NbApp nb = new NbApp().setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        return nb;
    }

    public void init() {
        init_sys();
        init_cms();
        init_task();
        ioc.get(Globals.class);
        Mvcs.X_POWERED_BY = "Budwk-V6 <budwk.com>";
        Mvcs.setDefaultLocalizationKey("zh-CN");
        Mvcs.setLocalizationManager(ioc.get(WkLocalizationManager.class));
        Globals.AppBase = Mvcs.getServletContext().getContextPath();
        Globals.AppRoot = Mvcs.getServletContext().getRealPath("/");
    }

    public void init_sys() {
        // 通过POJO类创建表结构
        try {
            Daos.createTablesInPackage(dao, "com.budwk.nb", false);
        } catch (Exception e) {
            log.error(e);
        }

        // 若必要的数据表不存在，则初始化数据库
        if (0 == dao.count(Sys_user.class)) {
            //初始化多语言表
            Sys_lang_local langLocal = new Sys_lang_local();
            langLocal.setName("中文");
            langLocal.setLocale("zh-CN");
            langLocal.setDisabled(false);
            dao.insert(langLocal);
            langLocal = new Sys_lang_local();
            langLocal.setName("英文");
            langLocal.setLocale("en-US");
            langLocal.setDisabled(false);
            dao.insert(langLocal);
            //初始化配置表
            Sys_config conf = new Sys_config();
            conf.setConfigKey("AppName");
            conf.setConfigValue("BudWk-V6");
            conf.setNote("系统名称");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppShrotName");
            conf.setConfigValue("BudWk");
            conf.setNote("系统短名称");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppDomain");
            conf.setConfigValue("http://127.0.0.1:8080");
            conf.setNote("系统域名");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppFileDomain");
            conf.setConfigValue("");
            conf.setNote("文件访问域名");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppUploadBase");
            conf.setConfigValue("/upload");
            conf.setNote("文件访问路径");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppWebUserOnlyOne");
            conf.setConfigValue("true");
            conf.setNote("用户单一登陆");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppWebBrowserNotice");
            conf.setConfigValue("false");
            conf.setNote("启用浏览器通知");
            dao.insert(conf);
            conf = new Sys_config();
            conf.setConfigKey("AppDemoEnv");
            conf.setConfigValue("false");
            conf.setNote("是否演示环境");
            dao.insert(conf);
            //初始化单位
            Sys_unit unit = new Sys_unit();
            unit.setPath("0001");
            unit.setName("系统管理");
            unit.setAliasName("System");
            unit.setLocation(0);
            unit.setAddress("银河-太阳系-地球");
            unit.setEmail("wizzer@qq.com");
            unit.setTelephone("");
            unit.setHasChildren(false);
            unit.setParentId("");
            unit.setWebsite("https://wizzer.cn");
            Sys_unit dbunit = dao.insert(unit);

            //初始化角色
            Sys_role role = new Sys_role();
            role.setName("公共角色");
            role.setCode("public");
            role.setNote("All user has role");
            role.setUnitid("");
            role.setDisabled(false);
            dao.insert(role);
            role = new Sys_role();
            role.setName("系统管理员");
            role.setCode(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME);
            role.setNote("System Admin");
            role.setUnitid("");
            role.setDisabled(false);
            Sys_role dbrole = dao.insert(role);
            //初始化用户
            Sys_user user = new Sys_user();
            user.setId("5f8cebd7022c409a94e90da1d840b8bb");
            user.setLoginname(PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME);
            user.setUsername("超级管理员");
            user.setSalt("r5tdr01s7uglfokpsdmtu15602");
            user.setPassword("1bba9287ebc50b766bff84273d11ccefaa7a8da95d078960f05f116e9d970fb0");
            user.setLoginIp("127.0.0.1");
            user.setLoginAt(0L);
            user.setLoginCount(0);
            user.setEmail("wizzer@qq.com");
            user.setUnitid(dbunit.getId());
            Sys_user dbuser = dao.insert(user);
            //不同的插入数据方式(安全)
            dao.insert("sys_user_unit", org.nutz.dao.Chain.make("userId", dbuser.getId()).add("unitId", dbunit.getId()));
            dao.insert("sys_user_role", org.nutz.dao.Chain.make("userId", dbuser.getId()).add("roleId", dbrole.getId()));
            //执行SQL脚本
            FileSqlManager fm = new FileSqlManager("db/");
            fm.setByRow(true);
            List<Sql> sqlList = fm.createCombo(fm.keys());
            Sql[] sqls = sqlList.toArray(new Sql[sqlList.size()]);
            for (Sql sql : sqls) {
                dao.execute(sql);
            }
            //菜单关联到角色
            dao.execute(Sqls.create("INSERT INTO sys_role_menu(roleId,menuId) SELECT @roleId,id FROM sys_menu").setParam("roleId", dbrole.getId()));
        }
    }

    public void init_cms() {
        //注册主键生成器
        //CustomMake.me().register("ig", ioc.get(RedisIdGenerator.class));
        //通过POJO类创建表结构
        try {
            if (dao.count(Cms_site.class, Cnd.NEW()) == 0) {
                Cms_site site = new Cms_site();
                site.setId("site");
                site.setSite_name("演示站点");
                site.setSite_domain("https://budwk.com");
                dao.insert(site);
            }
        } catch (Exception e) {
        }
    }

    public void init_task() {
        if (!dao.exists("sys_qrtz_triggers")&&!dao.exists("SYS_QRTZ_TRIGGERS")) {
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
