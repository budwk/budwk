package com.budwk.app.sys.providers;

import com.budwk.app.sys.services.SysLogService;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.log.enums.LogType;
import com.budwk.starter.log.model.Sys_log;
import com.budwk.starter.log.provider.ISysLogProvider;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.nutz.boot.starter.mongodb.plus.ZMongoClient;
import org.nutz.boot.starter.mongodb.plus.ZMongoDatabase;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

import java.util.LinkedList;
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

    private ZMongoClient zMongoClient;

    public void init() {
        if ("database".equalsIgnoreCase(conf.get("log.save"))) {
            // 初始化表结构
            sysLogService.logDao();
        } else if ("mongodb".equalsIgnoreCase(conf.get("log.save"))) {
            this.zMongoClient = ioc.get(ZMongoClient.class);
        }
    }

    @Override
    public void saveLog(Sys_log sysLog) {
        // 因为el表达式只对dao有效,所以这里手动设置雪花ID
        sysLog.setId(R.UU32());
        if ("database".equalsIgnoreCase(conf.get("log.save"))) {
            sysLogService.save(sysLog);
        } else if ("mongodb".equalsIgnoreCase(conf.get("log.save"))) {
            getCollection(Sys_log.class.getSimpleName()).insertOne(new Document(Lang.obj2map(sysLog)));
        }
    }

    @Override
    public Pagination list(String status, LogType type, String appId, String tag, String msg, String userId, String loginname, String username, long startTime, long endTime, String pageOrderName, String pageOrderBy, int pageNumber, int pageSize) {
        if ("database".equalsIgnoreCase(conf.get("log.save"))) {
            return sysLogService.list(status, type, appId, tag, msg, userId, loginname, username, startTime, endTime, pageOrderName, pageOrderBy, pageNumber, pageSize);
        } else if ("mongodb".equalsIgnoreCase(conf.get("log.save"))) {
            MongoCollection<Document> mongoCollection = getCollection(Sys_log.class.getSimpleName());

            Pagination pagination = new Pagination(pageNumber, pageSize, 0);
            List<Bson> filters = new LinkedList<>();
            if (type != null) {
                filters.add(Filters.eq("type", type.getValue()));
            }
            if (Strings.isNotBlank(appId)) {
                filters.add(Filters.eq("appId", appId));
            }
            if (Strings.isNotBlank(userId)) {
                filters.add(Filters.eq("createdBy", userId));
            }
            if (startTime > 0) {
                filters.add(Filters.gte("createdAt", startTime));
            }
            if (endTime > 0) {
                filters.add(Filters.lte("createdAt", startTime));
            }
            if (Strings.isNotBlank(tag)) {
                filters.add(Filters.regex("tag", "/" + tag + "/"));
            }
            if (Strings.isNotBlank(msg)) {
                filters.add(Filters.regex("msg", "/" + msg + "/"));
            }
            if (Strings.isNotBlank(loginname)) {
                filters.add(Filters.regex("loginname", "/" + loginname + "/"));
            }
            long count = mongoCollection.countDocuments(Filters.and(filters));
            pagination.setTotalCount((int) count);
            FindIterable<Document> findIterable = mongoCollection.find(Filters.and(filters));
            findIterable.limit(pageSize);
            findIterable.skip((pageNumber - 1) * pageSize);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                if ("desc".equalsIgnoreCase(PageUtil.getOrder(pageOrderBy))) {
                    findIterable.sort(Sorts.descending(pageOrderName));
                }
                if ("asc".equalsIgnoreCase(PageUtil.getOrder(pageOrderBy))) {
                    findIterable.sort(Sorts.ascending(pageOrderName));
                }
            } else {
                findIterable.sort(Sorts.descending("createdAt"));
            }
            MongoCursor<Document> cursor = findIterable.cursor();
            List<Sys_log> data = new LinkedList<>();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                data.add(Lang.map2Object(doc, Sys_log.class));
            }
            pagination.setList(data);
            return pagination;
        }
        return new Pagination();
    }

    private MongoCollection<Document> getCollection(String collection_name) {
        ZMongoDatabase db = zMongoClient.db();
        MongoCollection<Document> collection;
        if (db.collectionExists(collection_name)) {
            collection = db.getNativeDB().getCollection(collection_name);
        } else {

            db.getNativeDB().createCollection(collection_name);
            collection = db.getNativeDB().getCollection(collection_name);
            collection.createIndexes(List.of(
                    new IndexModel(Indexes.descending("createdAt")),
                    new IndexModel(Indexes.hashed("createdBy"))
            ));
        }
        return collection;
    }
}
