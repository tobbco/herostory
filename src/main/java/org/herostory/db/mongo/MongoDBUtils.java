package org.herostory.db.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBUtils {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBUtils.class);

    private static final String CONNECTION_STRING = "mongodb://admin:12345678@127.0.0.1:27017";
    private static final String DATABASE_NAME = "herostory";
    private static final String COUNTER_COLLECTION_NAME = "sequence_counter";
    private static MongoClient mongoClient;
    private static CodecRegistry pojoCodecRegistry;


    public static void connect() {
        try {
            CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
            pojoCodecRegistry = fromRegistries(defaultCodecRegistry, fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString(CONNECTION_STRING))
                    .codecRegistry(pojoCodecRegistry)
                    .build();
            mongoClient = MongoClients.create(settings);
        } catch (MongoException e) {
            logger.error("Error connecting to MongoDB: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void insertDocument(String collectionName, T document) {
        MongoCollection<T> collection = getCollection(collectionName, (Class<T>) document.getClass());
        collection.insertOne(document);
    }

    public static <T> List<T> findDocuments(String collectionName, Bson filter, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(collectionName, clazz);
        List<T> results = new ArrayList<>();
        collection.find(filter).into(results);
        return results;
    }


    public static <T> void updateDocument(String collectionName, Bson filter, Bson update, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(collectionName, clazz);
        collection.updateOne(filter, update);
    }

    public static <T> void deleteDocument(String collectionName, Bson filter, Class<T> clazz) {
        MongoCollection<T> collection = getCollection(collectionName, clazz);
        collection.deleteOne(filter);
    }

    private static <T> MongoCollection<T> getCollection(String collectionName, Class<T> clazz) {
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME).withCodecRegistry(pojoCodecRegistry);
        return database.getCollection(collectionName, clazz);
    }

    /**
     * 获取下一个序列号
     * 该方法用于生成并返回一个自增的序列号，基于给定的序列名称
     * 它通过在MongoDB中更新序列文档来实现序列号的自增
     * 如果序列不存在，它会创建一个新的序列文档并从1开始计数
     *
     * @param seqName 序列名称，用于标识特定的序列
     * @return 下一个序列号如果序列不存在，返回1
     */
    public static int getNextSequence(String seqName) {
        // 获取计数器集合
        MongoCollection<Document> collection = getCollection(COUNTER_COLLECTION_NAME, Document.class);
        // 配置查找和更新选项，以确保在更新后返回文档
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        // 定义更新操作，将序列号自增1
        Bson update = Updates.inc("seq", 1);
        // 查找并更新序列号文档，如果不存在，则插入新的序列文档
        Document counter = collection.findOneAndUpdate(Filters.eq("_id", seqName), update, options);
        if (counter == null) {
            // 处理序列不存在的情况（首次创建）
            collection.insertOne(new Document("_id", seqName).append("seq", 1));
            return 1;
        }
        // 返回更新后的序列号
        return counter.getInteger("seq");
    }
}