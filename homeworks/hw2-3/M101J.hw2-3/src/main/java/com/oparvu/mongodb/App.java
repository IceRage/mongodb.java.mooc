package com.oparvu.mongodb;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

/**
 * Hello world!
 *
 */
public class App {
    
    public static void main( String[] args ) {
        MongoClient client = new MongoClient();
        
        MongoDatabase   database             = client.getDatabase("students");
        MongoCollection<Document> collection = database.getCollection("grades");
        
        List<Document> studentsHomeworks = getStudentsHomeworkGrades(collection);
        
        List<ObjectId> gradesToRemove = getListOfGradesToRemove(studentsHomeworks); 
        
        collection.deleteMany(new Document("_id", new Document("$in", gradesToRemove)));
        
        System.out.println(collection.count());
    }
    
    private static List<ObjectId> getListOfGradesToRemove(List<Document> studentsHomeworks) {
        Map<Integer, ObjectId> studentsLowestHomeworkGrades = new HashMap<>();
        
        for (Document oneStudentHomework : studentsHomeworks) {
            Integer  studentId   = oneStudentHomework.getInteger("student_id");
            ObjectId documentId  = oneStudentHomework.getObjectId("_id");
            
            if (studentsLowestHomeworkGrades.get(studentId) == null) {
                studentsLowestHomeworkGrades.put(studentId, documentId);
            }
        }
        
        return new ArrayList<ObjectId>(studentsLowestHomeworkGrades.values());
    }

    private static List<Document> getStudentsHomeworkGrades(MongoCollection<Document> collection) {
        Bson docFilter          = new Document().append("type", "homework");
        Bson docProjection      = Projections.fields(Projections.include("student_id"),
                                                     Projections.include("score"));
        Bson docSortCriteria    = new Document().append("student_id", 1)
                                                .append("score", 1);
        
        return (
            collection.find(docFilter)
                      .projection(docProjection)
                      .sort(docSortCriteria)
                      .into(new ArrayList<Document>())
        );
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
