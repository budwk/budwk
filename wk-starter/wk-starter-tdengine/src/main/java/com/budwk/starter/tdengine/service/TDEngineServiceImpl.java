package com.budwk.starter.tdengine.service;

import com.budwk.starter.tdengine.dto.TableInfo;
import com.budwk.starter.tdengine.maker.TDEngineSqlMaker;
import com.budwk.starter.tdengine.maker.TaosFetchMapCallback;
import com.budwk.starter.tdengine.maker.TaosQueryMapCallBack;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.util.NutMap;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wizzer@qq.com
 * @author caoshi
 */
@Slf4j
public class TDEngineServiceImpl<T> implements TDEngineService<T> {

    public TDEngineServiceImpl(Dao dao) {
        this.dao = dao;
    }

    private Dao dao;

    public Dao dao() {
        return this.dao;
    }

    protected ConcurrentHashMap<String, Map<String, TableInfo>> localColumnCache = new ConcurrentHashMap<>(20);

    @Override
    public boolean exist(String tableName) {
        boolean[] exist = {false};
        dao().run(conn -> {
            Statement stat = null;
            ResultSet rs = null;
            try {
                stat = conn.createStatement();
                rs = stat.executeQuery("describe " + tableName);
                if (rs.next())
                    exist[0] = true;
            } catch (SQLException e) {
            } finally {
                Daos.safeClose(stat, rs);
            }
        });
        return exist[0];
    }

    @Override
    public Map<String, TableInfo> describe(String tableName) {
        Map<String, TableInfo> col = new LinkedHashMap<>();
        dao().run(conn -> {
            Statement stat = null;
            ResultSet rs = null;
            try {
                stat = conn.createStatement();
                rs = stat.executeQuery("describe " + tableName);
                while (rs.next()) {
                    TableInfo tableInfo = new TableInfo();
                    tableInfo.setField(rs.getString(1));
                    tableInfo.setType(rs.getString(2));
                    tableInfo.setLength(rs.getInt(3));
                    tableInfo.setNote(rs.getString(4));
                    col.put(rs.getString(1), tableInfo);
                }

            } catch (SQLException e) {
            } finally {
                Daos.safeClose(stat, rs);
            }
        });
        return col;
    }


    @Override
    public Map<String, TableInfo> getColumnCache(String tableName) {
        Map<String, TableInfo> colMap = localColumnCache.get(tableName);
        if (colMap == null || Lang.isEmpty(colMap)) {
            colMap = this.describe(tableName);
            localColumnCache.put(tableName, colMap);
        }
        return colMap;
    }

    @Override
    public void columnCacheClear(String tableName) {
        this.localColumnCache.remove(tableName);
    }

    @Override
    public void insert(String tableName, String subTableName, NutMap data) {
        Stopwatch st = new Stopwatch();
        st.start();

        Map<String, TableInfo> tableColumn = this.getColumnCache(tableName);
        if (Lang.isEmpty(tableColumn)) {
            throw new RuntimeException("table" + tableName + " is not exist");
        }

        Iterator<Map.Entry<String, TableInfo>> iterator =
                tableColumn.entrySet().iterator();
        StringBuilder fieldsCon = new StringBuilder(200);
        StringBuilder tagsCon = new StringBuilder(50);
        while (iterator.hasNext()) {
            Map.Entry<String, TableInfo> entry = iterator.next();
            TableInfo tableInfo = entry.getValue();
            Object value = data.get(entry.getKey());
            if (!tableInfo.getNote().equalsIgnoreCase("TAG")) {
                if (value == null) {
                    fieldsCon.append("null,");
                } else {
                    if (tableInfo.getType().equalsIgnoreCase("INT")) {
                        fieldsCon.append(value);
                        fieldsCon.append(",");
                    } else if (tableInfo.getType().equalsIgnoreCase("FLOAT")) {
                        fieldsCon.append(value);
                        fieldsCon.append(",");
                    } else if (tableInfo.getType().equalsIgnoreCase("TIMESTAMP")) {
                        fieldsCon.append(value);
                        fieldsCon.append(",");
                    } else {
                        fieldsCon.append("\'");
                        fieldsCon.append(value);
                        fieldsCon.append("\',");
                    }
                }
            } else { // TAGS
                if (value == null) {
                    tagsCon.append("null,");
                } else {
                    if (tableInfo.getType().equalsIgnoreCase("INT")) {
                        tagsCon.append(value);
                        tagsCon.append(",");
                    } else if (tableInfo.getType().equalsIgnoreCase("FLOAT")) {
                        tagsCon.append(value);
                        tagsCon.append(",");
                    } else if (tableInfo.getType().equalsIgnoreCase("TIMESTAMP")) {
                        tagsCon.append(value);
                        tagsCon.append(",");
                    } else {
                        tagsCon.append("\'");
                        tagsCon.append(value);
                        tagsCon.append("\',");
                    }
                }
            }
        }
        tagsCon.setCharAt(tagsCon.length() - 1, ' ');
        fieldsCon.setCharAt(fieldsCon.length() - 1, ' ');

        String sql = TDEngineSqlMaker.createInsertSql(subTableName, tableName, fieldsCon.toString(), tagsCon.toString());
        dao().execute(Sqls.create(sql));
        st.stop();
        log.debug("TDEngine 插入耗时：{} ms", st.du());
    }

    @Override
    public void createTable(String tableName, Class<T> tableClass, List<NutMap> fieldsList) {
        try {
            AtomicReference<Throwable> throwable = new AtomicReference<>();
            CompletableFuture.runAsync(() -> {
                        if (this.exist(tableName)) {
                            return;
                        }
                        StringBuilder sql = new StringBuilder(200);
                        StringBuilder fieldStr = new StringBuilder(30);
                        StringJoiner tags = new StringJoiner(",", "", "");

                        Mirror<?> mir = Mirror.me(tableClass);
                        Field[] fields = mir.getFields();
                        for (Field it : fields) {
                            ColDefine colDefine = it.getAnnotation(ColDefine.class);
                            ColType type = colDefine.type();
                            int width = colDefine.width();
                            String customType = colDefine.customType();

                            fieldStr.setLength(0);
                            fieldStr.append(it.getName());
                            fieldStr.append(" ");

                            switch (type) {
                                case INT:
                                    if (width == 1) {
                                        fieldStr.append("TINYINT");
                                    } else if (width == 2) {
                                        fieldStr.append("SMALLINT");
                                    } else if (width == 4) {
                                        fieldStr.append("INT");
                                    } else {
                                        fieldStr.append("BIGINT");
                                    }
                                    break;
                                case VARCHAR:
                                    fieldStr.append("BINARY");
                                    fieldStr.append("(");
                                    fieldStr.append(width);
                                    fieldStr.append(")");
                                    break;
                                case BOOLEAN:
                                    fieldStr.append("BOOL");
                                default:
                                    fieldStr.append(type.name());
                                    break;
                            }

                            if (customType.equalsIgnoreCase("TAG")) {
                                tags.add(fieldStr);
                            } else if (null != it.getAnnotation(Id.class)) {
                                fieldStr.append(",");
                                sql.insert(0, fieldStr);
                            } else {
                                fieldStr.append(",");
                                sql.append(fieldStr);
                            }
                        }

                        for (NutMap it : fieldsList) {
                            fieldStr.setLength(0);
                            Integer dataLen = it.getInt("dataLen", 0);
                            fieldStr.append(it.getString("code"));
                            fieldStr.append(" ");
                            switch (it.getString("dataType", "")) {
                                case "INT":
                                    if (dataLen == 2) {
                                        fieldStr.append("SMALLINT");
                                    } else if (dataLen == 4) {
                                        fieldStr.append("INT");
                                    } else {
                                        fieldStr.append("BIGINT");
                                    }
                                    break;
                                case "FLOAT":
                                    if (dataLen == 8) {
                                        fieldStr.append("DOUBLE");
                                    } else {
                                        fieldStr.append("FLOAT");
                                    }
                                    break;
                                case "TIMESTAMP":
                                    fieldStr.append("TIMESTAMP");
                                    break;
                                default:
                                    fieldStr.append("BINARY");
                                    fieldStr.append("(");
                                    fieldStr.append(dataLen);
                                    fieldStr.append(")");
                                    break;
                            }
                            fieldStr.append(",");
                            sql.append(fieldStr);
                        }
                        sql.setCharAt(sql.length() - 1, ' ');

                        String superTableSql = TDEngineSqlMaker.createSuperTableSql(tableName, sql.toString(), tags.toString());
                        dao().execute(Sqls.create(superTableSql));
                    }).whenComplete((unused, throwable1) -> {
                        throwable.set(throwable1);
                    })
                    .join();

            if (throwable.get() != null) {
                throw new RuntimeException(throwable.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<NutMap> list(String tableName, Condition cnd, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        int offset = pageSize * (pageNumber - 1);
        Sql sql = Sqls.create("select * from $table $condition limit $offset,$pageSize");
        sql.setCallback(new TaosQueryMapCallBack());
        sql.setVar("table", tableName);
        sql.setVar("offset", offset);
        sql.setVar("pageSize", pageSize);
        sql.setCondition(cnd);
        dao().execute(sql);
        return sql.getList(NutMap.class);
    }

    @Override
    public NutMap fetch(String tableName, String fields, Condition cnd) {
        Sql sql = Sqls.create("select $fields from $table $condition limit 1");
        sql.setVar("fields", fields);
        sql.setVar("table", tableName);
        sql.setCondition(cnd);
        sql.setCallback(new TaosFetchMapCallback());
        dao().execute(sql);
        return sql.getObject(NutMap.class);
    }

    @Override
    public long count(String tableName, Condition cnd) {
        Sql sql = Sqls.create("select count(*) from $table $condition");
        sql.setCallback(Sqls.callback.longValue());
        sql.setVar("table", tableName);
        sql.setCondition(cnd);
        dao().execute(sql);
        return sql.getLong(0);
    }
}
