import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.Trigger;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.avro.functions.*;

public class VerySimpleStreamingApp throws InterruptedException {
    //reading avro schema 
    private static final String jsonFormatSchema = new String(Files.readAllBytes(Paths.get("./src/main/resources/user.avsc")))

    public static void main(String[] args) {
        // Configure and initialize the SparkSession
        // you can any extra configuration here 
        SparkSession spark = SparkSession.builder()
                                         .appName("Streaming Application")
                                         .getOrCreate();

        // Receive streaming data from the source
        Dataset<Row> df = spark.readStream.format("kafka").option("kafka.bootstrap.servers", "b-3.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-1.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-2.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092").option("subscribe", "MSKTutorialTopic").option("startingOffsets", "earliest").load()
        // converting value in binary format to avro by passing schema
        Dataset<Row> output = df.select(from_avro(col("value"), jsonFormatSchema).as("record")).select("record.*")

        // do some transformation here 

        // you can change  below to write to rds , currently writing as csv to hdfs
        StreamingQuery query = output.writeStream
                                    .format("csv")
                                    .option("checkpointLocation", "/tmp/app")
                                    .option("path", "/output/")
                                    .trigger(Trigger.ProcessingTime("1 seconds"))
                                    .start()
        query.awaitTermination()
    }
}