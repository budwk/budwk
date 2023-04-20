package com.budwk.starter.tdengine.maker;

import org.nutz.lang.Strings;

import java.util.StringJoiner;

/**
 * @author wizzer@qq.com
 * @author caoshi
 */
public class TDEngineSqlMaker {

    private static final String CREATE_SUPER_TABLE = "create stable IF NOT EXISTS {stableName} ({fields}) tags ({tags})";

    private static final String INSERT_FULL_FIELD = "insert into {tableName} using {stableName} tags ({tagValues}) values ({fullFieldValues})";

    public static String createSuperTableSql(String tableName, String fields, String tags) {
        return CREATE_SUPER_TABLE
                .replace("{stableName}", tableName)
                .replace("{fields}", fields)
                .replace("{tags}", tags);
    }

    public static String buildTableName(String tableNamePrefix, String... names) {
        StringJoiner sj = new StringJoiner("_", "", "");
        sj.add(tableNamePrefix);
        for (String name : names) {
            if (Strings.isNotBlank(name))
                sj.add(name);
        }
        return sj.toString();
    }

    public static String createInsertSql(String subTableName, String superTableName, String values, String tagValue) {
        return INSERT_FULL_FIELD
                .replace("{tableName}", subTableName)
                .replace("{stableName}", superTableName)
                .replace("{tagValues}", tagValue)
                .replace("{fullFieldValues}", values);
    }
}
