package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.objects.query.CommandQuery;
import com.budwk.app.device.services.DeviceCommandRecordHistoryService;
import com.budwk.starter.common.page.Pagination;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.nutz.boot.starter.mongodb.plus.ZMongoClient;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author zyang  2022/5/30 11:43
 */
@IocBean(args = {"refer:zMongoClient"})
public class DeviceCommandRecordHistoryServiceImpl implements DeviceCommandRecordHistoryService {

    private static final String COLLECTION_NAME = Device_cmd_record.class.getSimpleName().toLowerCase(Locale.ROOT);
    private final ZMongoClient zMongoClient;


    public DeviceCommandRecordHistoryServiceImpl(@Inject ZMongoClient zMongoClient) {
        this.zMongoClient = zMongoClient;
    }

    @Override
    public void save(Device_cmd_record cmdRecord) {
        if (null == cmdRecord) {
            return;
        }
        getCollection(COLLECTION_NAME).insertOne(Document.parse(Json.toJson(cmdRecord, JsonFormat.tidy().ignoreJsonShape())));
    }

    @Override
    public void save(List<Device_cmd_record> cmdRecordList) {
        if (Lang.isEmpty(cmdRecordList)) {
            return;
        }
        getCollection(COLLECTION_NAME).insertMany(cmdRecordList
                .stream()
                .map(it -> Document.parse(Json.toJson(it, JsonFormat.tidy().ignoreJsonShape())))
                .collect(Collectors.toList()));
    }

    private MongoCollection<Document> getCollection(String collectionName) {
        return this.zMongoClient.db().createCollection(collectionName, false);
    }

    @Override
    public Pagination listPage(CommandQuery params) {
        Bson filter = buildFilter(params, null);

        MongoCollection<Document> collection = getCollection(COLLECTION_NAME);
        long total = collection.countDocuments(filter);
        List<Device_cmd_record> list = new ArrayList<>(params.getPageSize());
        if (total > 0) {
            FindIterable<Document> iterable = getCollection(COLLECTION_NAME).find(filter);
            iterable.sort(Sorts.descending("createdAt"));
            iterable.skip((params.getPageNo() - 1) * params.getPageSize()).limit(params.getPageSize());
            try (MongoCursor<Document> cursor = iterable.iterator()) {
                while (cursor.hasNext()) {
                    list.add(Json.fromJson(Device_cmd_record.class, cursor.next().toJson()));
                }
            }
        }
        return new Pagination(params.getPageNo(), params.getPageSize(), (int) total, list);
    }


    private Bson buildFilter(CommandQuery queryParam, List<String> ids) {
        List<Bson> filters = new ArrayList<>();
        if (Lang.isNotEmpty(ids)) {
            filters.add(Filters.in("id", ids));
        } else {
            filters.add(Filters.eq("productId", queryParam.getProductId()));
            if (Strings.isNotBlank(queryParam.getDeviceId())) {
                filters.add(Filters.eq("deviceId", queryParam.getDeviceId()));
            }
            if (queryParam.getStartDate() != null) {
                filters.add(Filters.gt("createdAt", queryParam.getStartDate()));
            }
            if (queryParam.getEndDate() != null) {
                filters.add(Filters.lte("createdAt", queryParam.getEndDate()));
            }
            if (Strings.isNotBlank(queryParam.getCommandId())) {
                filters.add(Filters.eq("commandId", queryParam.getCommandId()));
            }
            if (Strings.isNotBlank(queryParam.getKeyword())) {
                filters.add(Filters.or(Filters.eq("accountNo", queryParam.getKeyword()), Filters.eq("deviceNo", queryParam.getKeyword())));
            }
        }
        return Filters.and(filters);
    }
}

