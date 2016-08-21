package com.oparvu.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class RemoveLowestHomeworkScore {
    public static void main( String[] args ) {
        MongoClient                 client  = new MongoClient();
        MongoDatabase               db      = client.getDatabase("school");
        MongoCollection<Document>   coll    = db.getCollection("students");
        
        removeStudentsLowestHomeworkScore(coll);
        
        client.close();
    }

    private static void removeStudentsLowestHomeworkScore(MongoCollection<Document> coll) {
        List<Document> lowestHomeworkScores = getStudentsLowestHomeworkScores(coll);
        
        Bson documentFilter = null;
        Bson updateCriteria = null;
        
        for (Document oneLowestHomeworkScore : lowestHomeworkScores) {
            documentFilter = new Document("_id", oneLowestHomeworkScore.get("_id"));
            updateCriteria = new Document(
                                 "$pull", new Document(
                                     "scores", 
                                     new Document("type", "homework")
                                         .append("score", oneLowestHomeworkScore.get("minScore"))
                                 )
                             );
            
            coll.updateOne(documentFilter, updateCriteria);
        }
    }

    private static List<Document> getStudentsLowestHomeworkScores(MongoCollection<Document> coll) {
        List<Bson> aggregationPipeline = new ArrayList<>();
        
        Bson unwindAggregationStage = new Document("$unwind", "$scores");
        Bson matchAggregationStage  = new Document(
                                          "$match", 
                                          new Document("scores.type", "homework")
                                      );
        Bson groupAggregationStage  = new Document(
                                          "$group", new Document("_id", "$_id").append(
                                              "minScore", new Document("$min", "$scores.score")
                                          )
                                      );
        
        aggregationPipeline.add(unwindAggregationStage);
        aggregationPipeline.add(matchAggregationStage);
        aggregationPipeline.add(groupAggregationStage);
        
        return coll.aggregate(aggregationPipeline)
                   .into(new ArrayList<Document>());
    }
}
