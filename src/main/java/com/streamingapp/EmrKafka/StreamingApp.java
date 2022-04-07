package com.streamingapp.EmrKafka;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.avro.functions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public class StreamingApp {
   
    public static void main(String[] args) throws IOException, StreamingQueryException, TimeoutException {
        // Configure and initialize the SparkSession
        // you can any extra configuration here 
    	String jsonFormatSchema = new String(Files.readAllBytes(Paths.get("./user.avsc")));
        SparkSession spark = SparkSession.builder()
                                         .appName("Streaming Application")
                                         .getOrCreate();
        
        // Receive streaming data from the source
        Dataset<Row> df = spark.readStream().format("kafka").option("kafka.bootstrap.servers", "b-3.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-1.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-2.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092").option("subscribe", "MSKTutorialTopic").option("startingOffsets", "earliest").load();
        // converting value in binary format to avro by passing schema
        Dataset<Row> output = df.select(from_avro(col("value"), jsonFormatSchema).as("record")).select("record.*");

        // do some transformation here 

        // you can change  below to write to rds , currently writing as csv to hdfs
        StreamingQuery query = output.writeStream()
                                    .format("csv")
                                    .option("checkpointLocation", "/tmp/app")
                                    .option("path", "/output/")
                                    .trigger(Trigger.ProcessingTime("1 seconds"))
                                    .start();
        query.awaitTermination();
    }
}