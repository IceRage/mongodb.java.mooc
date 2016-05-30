package com.oparvu.mongodb;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Spark;

public class HelloWorldSparkFreemarkerStyle {

    public static void main(String[] args) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_19);
        
        configuration.setClassForTemplateLoading(
            HelloWorldSparkFreemarkerStyle.class, 
            "/"
        );

        Spark.get("/", (request, response) -> {
            StringWriter writer = new StringWriter();
            
            try {
                Template helloWorldTemplate  = configuration.getTemplate("hello.ftl");
                
                Map<String, Object> helloWorldMap = new HashMap<String, Object>();
                
                helloWorldMap.put("name", "John Doe");
                
                helloWorldTemplate.process(helloWorldMap, writer);
                
                System.out.println(writer);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            
            return writer;
        });
    }

}
