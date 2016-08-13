package com.oparvu.mongodb;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Sorts.*;

public class FindWithSortSkipLimitTest {

    public static void main(String[] args) {
        MongoClientOptions options  = MongoClientOptions.builder().connectionsPerHost(100).build();
        MongoClient        client   = new MongoClient(new ServerAddress(), options);
        
        MongoDatabase               db   = client.getDatabase("course");
        MongoCollection<Document>   coll = db.getCollection("insertTest");
        
        coll.drop();
        
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                coll.insertOne(new Document().append("i", i).append("j", j));
            }
        }
        
        // 0 means exclude the field
        // 1 means include the field
        Bson projection = fields(include("i", "j"), excludeId());
        
        // 1 means ascending sort
        // -1 means descending sort
        // Bson sort = new Document("i", 1).append("j", -1);
        Bson sort = descending("j", "i");
        
        List<Document> allDocs = coll.find()
                                     .projection(projection)
                                     .sort(sort)
                                     .skip(20)
                                     .limit(50)
                                     .into(new ArrayList<Document>());
        
        for (Document oneDoc : allDocs) {
            printJson(oneDoc);
        }
    }
    
    private static void printJson(Document document) {
        JsonWriter jsonWriter = new JsonWriter(new StringWriter(), 
                                               new JsonWriterSettings(JsonMode.SHELL, false));
        
        new DocumentCodec().encode(jsonWriter, document,
                                   EncoderContext.builder()
                                   .isEncodingCollectibleDocument(true)
                                   .build());
        
        System.out.println(jsonWriter.getWriter());
        System.out.flush();
    }
    
}
