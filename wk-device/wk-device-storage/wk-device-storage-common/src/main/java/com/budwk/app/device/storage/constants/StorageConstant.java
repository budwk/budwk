package com.budwk.app.device.storage.constants;

import org.bson.json.*;
/**
 * @author wizzer.cn
 */
public class StorageConstant {
    /**
     * Tdengine配置文件前缀
     */
    public static final String JDBC_TDENGINE_PREFIX = "jdbc.taos.";

    /**
     * 分割
     */
    public static final String UNDER_LINE = "_";

    /**
     * 空格
     */
    public static final String BLANK = " ";

    /**
     * 配置信息前缀
     */
    public static final String CONFIG_PREFIX = "device.storage.";
    /**
     * 通讯报文保存时间
     */
    public static final String RAW_DATA_TTL_CONFIG_KEY = CONFIG_PREFIX + "raw-save-ttl";
    /**
     * 通讯报文保存默认时间 30天
     */
    public static final long RAW_DATA_TTL_CONFIG_DEFAULT = 30;
    /**
     * 上报数据保存时间
     */
    public static final String REPORT_DATA_TTL_CONFIG_KEY = CONFIG_PREFIX + "data-save-ttl";
    /**
     * 上报数据保存默认时间 0=永久
     */
    public static final long REPORT_DATA_TTL_CONFIG_DEFAULT = 0;

}
