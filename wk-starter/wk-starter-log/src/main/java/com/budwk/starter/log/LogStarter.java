package com.budwk.starter.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.budwk.starter.log.logback.NutzJoranConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.slf4j.LoggerFactory;
import org.slf4j.TransmitLocalMDCAdapter;
import org.slf4j.impl.StaticLoggerBinder;

import java.net.URL;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
@Slf4j
public class LogStarter {

    protected static String PRE = "log.";

    @PropDoc(value = "Log SLog 日志存储方式", defaultValue = "database")
    public static final String PROP_SAVE = PRE + "save";

    @PropDoc(value = "Log Logback 日志文件存储路径", defaultValue = "logs")
    public static final String PROP_PATH = PRE + "path";

    @PropDoc(value = "Log Logback 日志等级", defaultValue = "info")
    public static final String PROP_LEVEL = PRE + "level";

    @PropDoc(value = "Log Logback 控制台彩色显示", defaultValue = "false", type = "boolean")
    public static final String PROP_COLOR = PRE + "color";


    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected AppContext appContext;

    @Inject
    protected PropertiesProxy conf;

    public void init() {
        System.out.println("load pid ...");
        conf.set("nutz.application.pid", Lang.JdkTool.getProcessId("0"));
        conf.set(PROP_PATH, conf.get(PROP_PATH, "logs"));
        URL fn = getClass().getClassLoader().getResource(conf.getBoolean(PROP_COLOR) ? "logback-color.xml" : "logback-no-color.xml");
        try {
            LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
            loggerContext.reset();
            NutzJoranConfigurator joranConfigurator = new NutzJoranConfigurator();
            joranConfigurator.setConf(conf);
            joranConfigurator.setContext(loggerContext);
            joranConfigurator.doConfigure(fn);
            loggerContext.getLogger("root").setLevel(Level.valueOf(conf.get(PROP_LEVEL,"info")));
            TransmitLocalMDCAdapter.getInstance();
            log.info("loaded slf4j configure file from {}", fn);
        } catch (JoranException e) {
            log.error("can loading slf4j configure file from " + fn, e);
        }
    }

}
