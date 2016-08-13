package com.oparvu.mongodb;

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
import static com.mongodb.client.model.Filters.*;

public class FindWithFilterTest {

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
            );
        }
        
//        Bson filter = new Document("x", 0)
//                        .append("y", new Document("$gt", 10).append("$lt", 90));
        
        Bson filter = and(eq("x", 0), gt("y", 10), lt("y", 90));
        
        List<Document> allDocs = coll.find(filter).into(new ArrayList<Document>());
        
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
