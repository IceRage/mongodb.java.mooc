package com.oparvu.mongodb;

import java.io.StringWriter;
import java.util.Arrays;

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
import com.mongodb.client.MongoDatabase;

public class InsertTest {

    public static void main(String[] args) {
        MongoClientOptions options  = MongoClientOptions.builder().connectionsPerHost(100).build();
        MongoClient        client   = new MongoClient(new ServerAddress(), options);
        
        MongoDatabase               db   = client.getDatabase("course");
        MongoCollection<Document>   coll = db.getCollection("insertTest");
        
        coll.drop();
        
        Document smith = new Document("name", "Smith")
                            .append("age", 30)
                            .append("profession", "programmer");
        
        Document jones = new Document("name", "Jones")
                            .append("age", 25)
                            .append("profession", "hacker");
        
        coll.insertMany(Arrays.asList(smith));
        
        coll.insertOne(jones);
        jones.remove("_id");
        coll.insertOne(jones);
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
