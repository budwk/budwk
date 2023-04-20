package com.budwk.starter.tdengine;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.jdbc.DataSourceStarter;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.sql.DataSource;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class TDEngineStarter {
    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected static final String PRE = "tdengine.";

    @PropDoc(value = "TDengine 是否启用数据库功能", defaultValue = "false", type = "boolean")
    public static final String PROP_TDENGINE_ENABLE = PRE + "enable";

    public void init() {
        if (!conf.getBoolean(PROP_TDENGINE_ENABLE, false)) {
            return;
        }
        DataSource taos = DataSourceStarter.createManyDataSource(ioc, conf, PRE + "jdbc.");
        ioc.addBean("taos", new NutDao(taos));
    }

}
