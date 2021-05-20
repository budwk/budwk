package com.budwk.starter.database;

import com.budwk.starter.database.ig.SnowFlakeIdGenerator;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.el.opt.custom.CustomMake;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class DatabaseStarter {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected static final String PRE = "database.";

    @PropDoc(value = "Database 是否启用数据库功能", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_ENABLE = PRE + "enable";

    @PropDoc(value = "Database 是否启用雪花主键(如启用必须连接redis)", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_IG_SNOWFLAKE = PRE + "ig.snowflake";

    @PropDoc(value = "Database 是否检查字段为数据库的关键字", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_GLOBAL_CHECKCOLUMNNAMEKEYWORD = PRE + "global.checkColumnNameKeyword";

    @PropDoc(value = "Database 是否把字段名用字符包裹来进行关键字逃逸", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_GLOBAL_FORCEWRAPCOLUMNNAME = PRE + "global.forceWrapColumnName";

    @PropDoc(value = "Database 是否把字段名给变成大写", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_GLOBAL_FORCEUPPERCOLUMNNAME = PRE + "global.forceUpperColumnName";

    @PropDoc(value = "Database 是否把字段名给变成驼峰", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_GLOBAL_FORCEHUMPCOLUMNNAME = PRE + "global.forceHumpColumnName";

    @PropDoc(value = "Database 是否自动建表", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_TABLE_CREATE = PRE + "table.create";

    @PropDoc(value = "Database 是否自动变更表结构", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_TABLE_MIGRATION = PRE + "table.migration";

    @PropDoc(value = "Database 变更表结构是否新增字段", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_TABLE_ADD = PRE + "table.add";

    @PropDoc(value = "Database 变更表结构是否删除字段", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_TABLE_DELETE = PRE + "table.delete";

    @PropDoc(value = "Database 变更表结构是否检查索引", defaultValue = "false", type = "boolean")
    public static final String PROP_DATABASE_TABLE_CHECK = PRE + "table.check";

    @PropDoc(value = "Database 扫描的包名", defaultValue = "")
    public static final String PROP_DATABASE_TABLE_PACKAGE = PRE + "table.package";

    public void init() {
        if (!conf.getBoolean(PROP_DATABASE_ENABLE, false)) {
            return;
        }
        Dao dao = ioc.get(Dao.class);
        if (conf.getBoolean(PROP_DATABASE_GLOBAL_CHECKCOLUMNNAMEKEYWORD, false)) {
            Daos.CHECK_COLUMN_NAME_KEYWORD = true;
        }
        if (conf.getBoolean(PROP_DATABASE_GLOBAL_FORCEWRAPCOLUMNNAME, false)) {
            Daos.FORCE_WRAP_COLUMN_NAME = true;
        }
        if (conf.getBoolean(PROP_DATABASE_GLOBAL_FORCEUPPERCOLUMNNAME, false)) {
            Daos.FORCE_UPPER_COLUMN_NAME = true;
        }
        if (conf.getBoolean(PROP_DATABASE_GLOBAL_FORCEHUMPCOLUMNNAME, false)) {
            Daos.FORCE_HUMP_COLUMN_NAME = true;
        }
        List<String> packages = conf.getList(PROP_DATABASE_TABLE_PACKAGE);
        packages.forEach(pkg -> {
            if (conf.getBoolean(PROP_DATABASE_TABLE_CREATE, false)) {
                Daos.createTablesInPackage(dao, pkg, false);
            }
            if (conf.getBoolean(PROP_DATABASE_TABLE_MIGRATION, false)) {
                Daos.migration(dao, pkg, conf.getBoolean(PROP_DATABASE_TABLE_ADD, false),
                        conf.getBoolean(PROP_DATABASE_TABLE_DELETE, false),
                        conf.getBoolean(PROP_DATABASE_TABLE_CHECK, false));
            }
        });
    }
}
