package com.oparvu.mongodb;

import static com.mongodb.client.model.Filters.*;

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
import com.mongodb.client.MongoDatabase;

public class DeleteTest {

    public static void main(String[] args) {
        MongoClientOptions options  = MongoClientOptions.builder().connectionsPerHost(100).build();
        MongoClient        client   = new MongoClient(new ServerAddress(), options);
        
        MongoDatabase               db   = client.getDatabase("course");
        MongoCollection<Document>   coll = db.getCollection("insertTest");
        
        coll.drop();
        
        for (int i = 0; i < 10; ++i) {
            coll.insertOne(
                new Document().append("_id", i)
            );
        }
        
        coll.deleteOne(eq("_id", 4));
        
        List<Document> allDocs = coll.find().into(new ArrayList<Document>());
        
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
