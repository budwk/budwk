package com.budwk.app.device.storage.storages;

import com.budwk.app.device.storage.constants.StorageConstant;
import com.budwk.app.device.storage.objects.dto.DeviceRawDataDTO;
import com.budwk.app.device.storage.objects.query.DeviceRawDataQuery;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.nutz.boot.starter.mongodb.plus.ZMongoClient;
import org.nutz.boot.starter.mongodb.plus.ZMongoDatabase;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class MongoDeviceRawDataStorageImpl implements DeviceRawDataStorage {
    public static final String collection_name = "device_raw_data";
    @Inject
    private ZMongoClient zMongoClient;
    @Inject
    private PropertiesProxy conf;

    @Override
    public void save(DeviceRawDataDTO data) {
        Document document = new Document();
        data.setTs(System.currentTimeMillis());
        document.putAll(Lang.obj2map(data));
        getCollection().insertOne(document);
    }

    @Override
    public List<DeviceRawDataDTO> list(DeviceRawDataQuery query) {
        return null;
    }

    @Override
    public long count(DeviceRawDataQuery query) {
        Bson cnd = Filters.and(this.buildConditions(query));
        return getCollection().countDocuments(cnd);
    }

    protected List<Bson> buildConditions(DeviceRawDataQuery query) {
        List<Bson> conditions = new LinkedList<>();
        conditions.add(Filters.eq("deviceId", query.getDeviceId()));
        if (query.getStartTime() != null) {
            conditions.add(Filters.gte("startTime", query.getStartTime()));
        }
        if (query.getEndTime() != null) {
            conditions.add(Filters.lte("startTime", query.getEndTime()));
        }
        if (Strings.isNotBlank(query.getIds())) {
            List<ObjectId> ids = Arrays.stream(query.getIds().split(",")).map(i -> new ObjectId(i)).collect(Collectors.toList());
            conditions.add(Filters.in("_id", ids));
        }
        return conditions;
    }

    private MongoCollection<Document> getCollection() {
        ZMongoDatabase db = zMongoClient.db();
        MongoCollection<Document> collection;
        if (db.collectionExists(collection_name)) {
            collection = db.getNativeDB().getCollection(collection_name);
        } else {
            log.debug("集合 {} 不存在, 重新创建", collection_name);
            db.getNativeDB().createCollection(collection_name);
            collection = db.getNativeDB().getCollection(collection_name);
            collection.createIndexes(List.of(
                    new IndexModel(Indexes.descending("startTime", "endTime")),
                    new IndexModel(Indexes.hashed("deviceId")),
                    new IndexModel(Indexes.descending("ts"),
                            new IndexOptions()
                                    .expireAfter(3600 * 24 * conf.getLong(StorageConstant.RAW_DATA_TTL_CONFIG_KEY,
                                            StorageConstant.RAW_DATA_TTL_CONFIG_DEFAULT), TimeUnit.SECONDS))
            ));
        }
        return collection;
    }
}
