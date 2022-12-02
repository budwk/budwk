package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.services.SysLogService;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseServiceImpl;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.log.model.Sys_log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@Slf4j
public class SysLogServiceImpl extends BaseServiceImpl<Sys_log> implements SysLogService {
    public SysLogServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 按月分表的dao实例
     */
    protected Map<String, Dao> ymDaos = new HashMap<String, Dao>();

    /**
     * 获取按月分表的Dao实例,即当前日期的dao实例
     *
     * @return
     */
    public Dao logDao() {
        Calendar cal = Calendar.getInstance();
        String key = String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        return logDao(key);
    }

    /**
     * 获取特定月份的Dao实例
     *
     * @param key
     * @return
     */
    public Dao logDao(String key) {
        Dao dao = ymDaos.get(key);
        if (dao == null) {
            synchronized (this) {
                dao = ymDaos.get(key);
                if (dao == null) {
                    dao = Daos.ext(this.dao(), key);
                    dao.create(Sys_log.class, false);
                    ymDaos.put(key, dao);
                }
            }
        }
        return dao;
    }

    @Override
    public void save(Sys_log syslog) {
        logDao().insert(syslog);
    }

    @Override
    public Pagination list(String status, LogType type, String appId, String tag, String msg, String userId, String loginname, String username, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize) {
        try {
            String tableName = Times.format("yyyyMM", new Date());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select sl.* from (");
            if (startTime == 0 && endTime == 0) {
                stringBuilder.append(" select * from sys_log_" + tableName + " where 1=1 ");
                if (type != null) {
                    stringBuilder.append(" and type='" + type.getValue() + "'");
                }
                if (Strings.isNotBlank(appId)) {
                    stringBuilder.append(" and appId = '" + appId + "'");
                }
                if (Strings.isNotBlank(userId)) {
                    stringBuilder.append(" and createdBy = '" + userId + "'");
                }
                if (Strings.isNotBlank(loginname)) {
                    stringBuilder.append(" and loginname like '%" + loginname + "%'");
                }
                if (Strings.isNotBlank(tag)) {
                    stringBuilder.append(" and tag like '%" + tag + "%'");
                }
                if (Strings.isNotBlank(msg)) {
                    stringBuilder.append(" and msg like '%" + msg + "%'");
                }
                if (Strings.isNotBlank(username)) {
                    stringBuilder.append(" and username like '%" + username + "%'");
                }
                if ("success".equalsIgnoreCase(status)) {
                    stringBuilder.append(" and exception is null");
                }
                if ("exception".equalsIgnoreCase(status)) {
                    stringBuilder.append(" and exception is not null");
                }
            } else {
                int m1 = 0;
                int m2 = 0;
                if (startTime == 0) {
                    m1 = NumberUtils.toInt(Times.format("yyyyMM", new Date()));
                } else {
                    m1 = NumberUtils.toInt(Times.format("yyyyMM", Times.D(startTime)));
                }
                if (endTime == 0) {
                    m2 = NumberUtils.toInt(Times.format("yyyyMM", new Date()));
                } else {
                    m2 = NumberUtils.toInt(Times.format("yyyyMM", Times.D(endTime)));
                }
                if (m1 == m2) {
                    stringBuilder.append(" select * from sys_log_" + m1 + " where 1=1 ");
                    if (type != null) {
                        stringBuilder.append(" and type='" + type.getValue() + "'");
                    }
                    if (Strings.isNotBlank(appId)) {
                        stringBuilder.append(" and appId = '" + appId + "'");
                    }
                    if (Strings.isNotBlank(userId)) {
                        stringBuilder.append(" and createdBy = '" + userId + "'");
                    }
                    if (Strings.isNotBlank(loginname)) {
                        stringBuilder.append(" and loginname like '%" + loginname + "%'");
                    }
                    if (Strings.isNotBlank(tag)) {
                        stringBuilder.append(" and tag like '%" + tag + "%'");
                    }
                    if (Strings.isNotBlank(msg)) {
                        stringBuilder.append(" and msg like '%" + msg + "%'");
                    }
                    if (Strings.isNotBlank(username)) {
                        stringBuilder.append(" and username like '%" + username + "%'");
                    }
                    if ("success".equalsIgnoreCase(status)) {
                        stringBuilder.append(" and exception is null");
                    }
                    if ("exception".equalsIgnoreCase(status)) {
                        stringBuilder.append(" and exception is not null");
                    }
                    if(startTime>0) {
                        stringBuilder.append(" and createdAt>=" + startTime);
                    }
                    if(endTime>0) {
                        stringBuilder.append(" and createdAt<=" + endTime);
                    }
                } else {
                    for (int i = m1; i < m2 + 1; i++) {
                        if (this.dao().exists("sys_log_" + i)) {
                            stringBuilder.append(" select * from sys_log_" + i + " where 1=1 ");
                            if (type != null) {
                                stringBuilder.append(" and type='" + type.getValue() + "'");
                            }
                            if (Strings.isNotBlank(appId)) {
                                stringBuilder.append(" and appId = '" + appId + "'");
                            }
                            if (Strings.isNotBlank(userId)) {
                                stringBuilder.append(" and createdBy = '" + userId + "'");
                            }
                            if (Strings.isNotBlank(loginname)) {
                                stringBuilder.append(" and loginname like '%" + loginname + "%'");
                            }
                            if (Strings.isNotBlank(tag)) {
                                stringBuilder.append(" and tag like '%" + tag + "%'");
                            }
                            if (Strings.isNotBlank(msg)) {
                                stringBuilder.append(" and msg like '%" + msg + "%'");
                            }
                            if (Strings.isNotBlank(username)) {
                                stringBuilder.append(" and username like '%" + username + "%'");
                            }
                            if ("success".equalsIgnoreCase(status)) {
                                stringBuilder.append(" and exception is null");
                            }
                            if ("exception".equalsIgnoreCase(status)) {
                                stringBuilder.append(" and exception is not null");
                            }
                            if(startTime>0) {
                                stringBuilder.append(" and createdAt>=" + startTime);
                            }
                            if(endTime>0) {
                                stringBuilder.append(" and createdAt<=" + endTime);
                            }
                            if (i < m2) {
                                stringBuilder.append(" UNION ALL ");
                            }
                        }
                    }
                }
            }
            stringBuilder.append(")sl ");
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                stringBuilder.append(" order by sl." + pageOrderName + " " + PageUtil.getOrder(pageOrderBy));
            }
            // 使用listPage 字段名全是小写,这里使用 listPageMap 区分大小写做到与mongodb返回的字段一致,这样不影响前端排序
            return this.listPageMap(pageNumber, pageSize, Sqls.create(stringBuilder.toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Pagination();
        }
    }
}
