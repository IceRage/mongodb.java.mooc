package com.oparvu.mongodb;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class FindTest {

    public static void main(String[] args) {
        MongoClientOptions options  = MongoClientOptions.builder().connectionsPerHost(100).build();
        MongoClient        client   = new MongoClient(new ServerAddress(), options);
        
        MongoDatabase               db   = client.getDatabase("course");
        MongoCollection<Document>   coll = db.getCollection("insertTest");
        
        coll.drop();
        
        for (int i = 0; i < 10; ++i) {
            coll.insertOne(new Document("x", i));
        }
        
        System.out.println("Find one: ");
        Document first = coll.find().first();
        printJson(first);
        
        System.out.println("Find all with into: ");
        List<Document> list = coll.find().into(new ArrayList<Document>());
        
        for (Document currDoc: list) {
            printJson(currDoc);
        }
        
        System.out.println("Find all with iteration: ");
        MongoCursor<Document> cursor = coll.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document currDoc = cursor.next();
                printJson(currDoc);
            }
        } finally {
            cursor.close();
        }
        
        System.out.println("Count: ");
        long count = coll.count();
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
