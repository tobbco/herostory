package org.herostory.db.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtils {

    private static final String CONNECTION_STRING = "mongodb://admin:12345678@192.168.5.2:27017"; // Replace with your connection string
    private static final String DATABASE_NAME = "herostory";       // Replace with your database name
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
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
            throw e; // Re-throw the exception for handling by calling method
        }
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

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
    public static int getNextSequence(String seqName) {
        MongoCollection<Document> collection = getCollection(COUNTER_COLLECTION_NAME, Document.class);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        Bson update = Updates.inc("seq", 1);
        Document counter = collection.findOneAndUpdate(Filters.eq("_id", seqName), update, options);
        if (counter == null) {
            // Handle case where counter doesn't exist (first time)
            collection.insertOne(new Document("_id", seqName).append("seq", 1));
            return 1;
        }
        return counter.getInteger("seq");
    }
}