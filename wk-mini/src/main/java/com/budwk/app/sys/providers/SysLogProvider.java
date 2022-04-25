package com.budwk.app.sys.providers;

import com.budwk.app.sys.services.SysLogService;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.log.model.Sys_log;
import com.budwk.starter.log.provider.ISysLogProvider;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mongo.ZMoCo;
import org.nutz.mongo.ZMoDB;
import org.nutz.mongo.ZMoDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class SysLogProvider implements ISysLogProvider {
    @Inject
    private SysLogService sysLogService;

    @Inject
    private PropertiesProxy conf;

    @Inject("refer:$ioc")
    private Ioc ioc;

    private ZMoDB zMoDB;

    private ZMoCo zMoCo;

    public void init() {
        if ("database".equalsIgnoreCase(conf.get("log.save"))) {
            // 初始化表结构
            sysLogService.logDao();
        } else if ("mongodb".equalsIgnoreCase(conf.get("log.save"))) {
            this.zMoDB = ioc.get(ZMoDB.class, "zmodb");
            this.zMoCo = zMoDB.cc(Sys_log.class.getSimpleName(), false);
        }
    }

    @Override
    public void saveLog(Sys_log sysLog) {
        // 因为el表达式只对dao有效,所以这里手动设置雪花ID
        sysLog.setId(R.UU32());
        if ("database".equalsIgnoreCase(conf.get("log.save"))) {
            sysLogService.save(sysLog);
        } else if ("mongodb".equalsIgnoreCase(conf.get("log.save"))) {
            zMoCo.insert(ZMoDoc.NEW(Json.toJson(sysLog, JsonFormat.tidy())).putv("_id", sysLog.getId()));
        }
    }

    @Override
    public Pagination list(LogType type, String appId, String tag, String msg, String loginname, String username, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize) {
        if ("database".equalsIgnoreCase(conf.get("log.save"))) {
            return sysLogService.list(type, appId, tag, msg, loginname, username, startTime, endTime, pageOrderName, pageOrderBy, pageNumber, pageSize);
        } else if ("mongodb".equalsIgnoreCase(conf.get("log.save"))) {
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
            if (type != null) {
                queryDoc.put("type", ZMoDoc.NEW("$regex", type.getValue()));
            }
            if (Strings.isNotBlank(appId)) {
                queryDoc.put("appId", ZMoDoc.NEW("$regex", appId));
            }
            if (Strings.isNotBlank(tag)) {
                queryDoc.put("tag", ZMoDoc.NEW("$regex", tag));
            }
            if (Strings.isNotBlank(msg)) {
                queryDoc.put("msg", ZMoDoc.NEW("$regex", msg));
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
                ZMoDoc sortDoc = ZMoDoc.NEW("createdAt", "asc".equals(pageOrderBy) ? 1 : -1);
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
