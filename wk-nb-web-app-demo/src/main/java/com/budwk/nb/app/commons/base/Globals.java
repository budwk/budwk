package com.budwk.nb.app.commons.base;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.sys.models.Sys_config;
import com.budwk.nb.sys.services.SysConfigService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2016/12/19.
 */
@IocBean(create = "init")
public class Globals {
    // 项目路径
    public static String AppRoot = "";
    // 项目目录
    public static String AppBase = "";
    // 项目名称
    public static String AppName = "BudWk V6";
    // 项目短名称
    public static String AppShrotName = "budwk";
    // 项目域名
    public static String AppDomain = "http://127.0.0.1";
    // 文件访问域名
    public static String AppFileDomain = "";
    // 文件上传路径
    public static String AppUploadBase = "/upload";
    // 默认微信ID
    public static String AppDefultWxID = "MAIN";
    // 系统自定义参数
    public static NutMap MyConfig = NutMap.NEW();
    // 微信map
    public static NutMap WxMap = NutMap.NEW();
    // 微信支付map
    public static NutMap WxPay3Map = NutMap.NEW();
    @Inject
    @Reference
    private SysConfigService sysConfigService;

    public void init() {
        initSysParam(sysConfigService);
    }

    public static void initSysParam(SysConfigService sysConfigService) {
        Globals.MyConfig.clear();
        List<Sys_config> configList = sysConfigService.query();
        for (Sys_config sysConfig : configList) {
            switch (Strings.sNull(sysConfig.getConfigKey())) {
                case "AppName":
                    Globals.AppName = sysConfig.getConfigValue();
                    break;
                case "AppShrotName":
                    Globals.AppShrotName = sysConfig.getConfigValue();
                    break;
                case "AppDomain":
                    Globals.AppDomain = sysConfig.getConfigValue();
                    break;
                case "AppFileDomain":
                    Globals.AppFileDomain = sysConfig.getConfigValue();
                    break;
                case "AppUploadBase":
                    Globals.AppUploadBase = sysConfig.getConfigValue();
                    break;
                case "AppDefultWxID":
                    Globals.AppDefultWxID = sysConfig.getConfigValue();
                    break;
                default:
                    break;
            }
            Globals.MyConfig.put(sysConfig.getConfigKey(), sysConfig.getConfigValue());
        }
    }

    public static void initWx() {
        Globals.WxMap.clear();
    }
}
