package com.oparvu.mongodb;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkFormHandling {

    public static void main(String[] args) {
        // Configure Freemarker
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_19);
        
        configuration.setClassForTemplateLoading(
            SparkFormHandling.class, 
            "/"
        );
        
        // Configure routes
        Spark.get("/", new Route() {
            
            @Override
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();
                
                try {
                    Map<String, Object> fruitMap = new HashMap<>();
                    
                    fruitMap.put("fruits", Arrays.asList("apple", "banana", "orange", "peach"));
                    
                    Template fruitPickerTemplate = configuration.getTemplate("fruitPicker.ftl");

                    fruitPickerTemplate.process(fruitMap, writer);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                
                return writer;
            }
        });
        
        Spark.post("/favorite_fruit", new Route() {

            @Override
            public Object handle(final Request request, final Response response) throws Exception {
                final String fruit = request.queryParams("fruit");
                
                if (fruit == null) {
                    return "Why don't you pick one?";
                } else {
                    return "Your favorite fruit is " + fruit;
                }
            }
            
        });
        
    }

}