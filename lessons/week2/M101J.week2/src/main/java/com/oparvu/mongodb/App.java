package com.oparvu.mongodb;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        Document document = new Document();
                
        document = document.append("str", "The presidential elections in USA")
                           .append("int", 42)
                           .append("l", 1L)
                           .append("d", 1.0)
                           .append("b", true)
                           .append("date", new Date())
                           .append("objectId", new ObjectId());
        
        String str = document.getString("str");
        
        System.out.println(str);
    }
    
    public static void mongoDBInto() {
        MongoClientOptions options  = MongoClientOptions.builder().connectionsPerHost(100).build();
        MongoClient        client   = new MongoClient(new ServerAddress(), options);
        
        MongoDatabase               db   = client.getDatabase("test").withReadPreference(ReadPreference.secondary());
        MongoCollection<Document>   coll = db.getCollection("test", Document.class);
    }
}
