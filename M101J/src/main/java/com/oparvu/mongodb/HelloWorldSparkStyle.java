package com.oparvu.mongodb;

import spark.Spark;

public class HelloWorldSparkStyle {

    public static void main(String[] args) {
        Spark.get("/", (request, response) -> {
            return "Hello World From Spark!";
        });
    }
    
}
