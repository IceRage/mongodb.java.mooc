package com.oparvu.mongodb;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;

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
import static com.mongodb.client.model.Projections.*;

public class FindWithProjectionTest {

    public static void main(String[] args) {
        MongoClientOptions options  = MongoClientOptions.builder().connectionsPerHost(100).build();
        MongoClient        client   = new MongoClient(new ServerAddress(), options);
        
        MongoDatabase               db   = client.getDatabase("course");
        MongoCollection<Document>   coll = db.getCollection("insertTest");
        
        coll.drop();
        
        for (int i = 0; i < 10; ++i) {
            coll.insertOne(
                new Document().append("x", new Random().nextInt(2))
                              .append("y", new Random().nextInt(100))
                              .append("i", i)
            );
        }
        
        Bson filter = and(eq("x", 0), gt("y", 10), lt("y", 90));
        
        // 0 means exclude the field
        // 1 means include the field
        // Bson projection = new Document("y", 1).append("i", 1).append("_id", 0);
        Bson projection = fields(include("y", "i"), excludeId());
        
        List<Document> allDocs = coll.find(filter)
                                     .projection(projection)
                                     .into(new ArrayList<Document>());
        
        for (Document oneDoc : allDocs) {
            printJson(oneDoc);
        }
        
        long count = coll.count(filter);
        System.out.println(count);
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
