package com.budwk.nb.sys.commons.core;

import com.budwk.nb.commons.constants.PlatformConstant;
import com.budwk.nb.sys.models.*;
import org.nutz.boot.NbApp;
import org.nutz.dao.Chain;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Modules;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2018/3/16.
 */
@IocBean(create = "init", depose = "depose")
@Modules(packages = "com.budwk.nb")
public class SysMainLauncher {
    private static final Log log = Logs.get();

    @Inject
    private Dao dao;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        nb.run();
    }

    public void init() {
        // 通过POJO类创建表结构
        try {
            Daos.createTablesInPackage(dao, "com.budwk.nb.sys", false);
        } catch (Exception e) {
            log.error(e);
        }

        // 若必要的数据表不存在，则初始化数据库
        if (0 == dao.count(Sys_user.class)) {
            //初始化多语言表
            Sys_lang_local langLocal=new Sys_lang_local();
            langLocal.setName("中文");
            langLocal.setLocale("zh-CN");
            langLocal.setDisabled(false);
            dao.insert(langLocal);
            langLocal=new Sys_lang_local();
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
            dao.insert("sys_user_unit", Chain.make("userId", dbuser.getId()).add("unitId", dbunit.getId()));
            dao.insert("sys_user_role", Chain.make("userId", dbuser.getId()).add("roleId", dbrole.getId()));
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

    public void depose() {
        // 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
        try {
            Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
        } catch (Throwable e) {
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
    }
}
