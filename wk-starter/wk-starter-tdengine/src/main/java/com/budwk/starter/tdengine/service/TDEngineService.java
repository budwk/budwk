package com.budwk.starter.tdengine.service;

import com.budwk.starter.tdengine.dto.TableInfo;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

/**
 * @author wizzer@qq.com
 * @author caoshi
 */
public interface TDEngineService<T> {
    /**
     * 获取dao对象
     *
     * @return
     */
    Dao dao();

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return
     */
    boolean exist(String tableName);

    /**
     * 获取表结构
     *
     * @param tableName 表名
     * @return
     */
    Map<String, TableInfo> describe(String tableName);

    /**
     * 获取表字段定义
     *
     * @param tableName
     * @return
     */
    Map<String, TableInfo> getColumnCache(String tableName);

    /**
     * 删除表字段缓存
     *
     * @param tableName
     */
    void columnCacheClear(String tableName);

    /**
     * 插入数据
     *
     * @param tableName    超级表名
     * @param subTableName 子表名
     * @param data         k=v
     */
    void insert(String tableName, String subTableName, NutMap data);

    /**
     * 创建超级表
     *
     * @param tableName  超级表名
     * @param tableClass 设备Pojo类
     * @param fieldsList 设备扩展字段
     */
    void createTable(String tableName, Class<T> tableClass, List<NutMap> fieldsList);

    /**
     * 查询列表数据
     *
     * @param tableName  超级表名
     * @param cnd        条件表达式
     * @param pageNumber 页码
     * @param pageSize   页大小
     * @return
     */
    List<NutMap> list(String tableName, Condition cnd, int pageNumber, int pageSize);

    /**
     * 查询一条记录
     *
     * @param tableName 超级表名
     * @param fields    返回字段,* 为所有字段
     * @param cnd       条件表达式
     * @return
     */
    NutMap fetch(String tableName, String fields, Condition cnd);

    /**
     * 查询条数
     *
     * @param tableName 超级表名
     * @param cnd       条件表达式
     * @return
     */
    long count(String tableName, Condition cnd);
}
