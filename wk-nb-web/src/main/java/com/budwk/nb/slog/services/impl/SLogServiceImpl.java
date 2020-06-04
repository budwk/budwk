package com.budwk.nb.slog.services.impl;

import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.slog.services.SLogSerivce;
import com.budwk.nb.sys.models.Sys_log;
import com.budwk.nb.sys.services.SysLogService;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mongo.ZMoCo;
import org.nutz.mongo.ZMoDB;
import org.nutz.mongo.ZMoDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2019/12/13
 */
@IocBean(create = "init")
public class SLogServiceImpl implements SLogSerivce {
    @Inject("java:$conf.get('slog.db.type')")
    private String dbType;
    @Inject
    private SysLogService sysLogService;
    @Inject("refer:$ioc")
    private Ioc ioc;
    private ZMoDB zMoDB;

    private ZMoCo zMoCo;

    public void init() {
        if ("mongo".equals(dbType)) {
            this.zMoDB = ioc.get(ZMoDB.class, "zmodb");
            this.zMoCo = zMoDB.cc(Sys_log.class.getSimpleName(), false);
        }
    }

    @Override
    public void create(Sys_log log) {
        if ("db".equals(dbType)) {
            sysLogService.fastInsertSysLog(log);
        }
        if ("mongo".equals(dbType)) {
            zMoCo.insert(ZMoDoc.NEW(Json.toJson(log, JsonFormat.tidy())).putv("_id", log.getId()));
        }
    }

    @Override
    public Pagination list(String type, String loginname, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize) {
        if ("db".equals(dbType)) {
            return sysLogService.list(type, loginname, startTime, endTime, pageOrderName, pageOrderBy, pageNumber, pageSize);
        }
        if ("mongo".equals(dbType)) {
            ZMoDoc queryDoc = ZMoDoc.NEW();
            ZMoDoc timeRange = ZMoDoc.NEW();
            Pagination pagination = new Pagination(pageNumber, pageSize, 0);
            if (startTime > 0) {
                timeRange.put("$gt", startTime);
            }
            if (endTime > 0) {
                timeRange.put("$lte", endTime);
            }
            if (!timeRange.isEmpty()) {
                queryDoc.put("createdAt", timeRange);
            }
            if (Strings.isNotBlank(type)) {
                queryDoc.put("type", ZMoDoc.NEW("$regex", type));
            }
            if (Strings.isNotBlank(loginname)) {
                queryDoc.put("loginname", ZMoDoc.NEW("$regex", loginname));
            }
            long count = zMoCo.count(queryDoc);
            pagination.setTotalCount((int) count);
            DBCursor cur = zMoCo.find(queryDoc);
            cur.skip((pageNumber - 1) * pageSize);
            cur.limit(pageSize);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                ZMoDoc sortDoc = ZMoDoc.NEW("createAt", "asc".equals(pageOrderBy) ? 1 : -1);
                cur.sort(sortDoc);
            }
            List<DBObject> objects = new ArrayList<>();
            while (cur.hasNext()) {
                DBObject obj = cur.next();
                objects.add(obj);
            }
            pagination.setList(objects);
            return pagination;
        }
        return new Pagination();
    }

}
