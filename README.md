# emr-kafka

author : karthik y

1) copy below jars to s3 bucket
   i)spark-sql-kafka-0-10_2.11-2.4.8.jar
   ii) kafka-clients-0.10.2.2.jar
  while creating EMR cluster , add below shell script  in bootstrap action which will copy s3 jar files to all nodes
  > sudo aws s3 cp s3://doc-example-bucket/spark-sql-kafka-0-10_2.11-2.4.8.jar /usr/lib/spark/jars/
  > sudo aws s3 cp s3://doc-example-bucket/kafka-clients-0.10.2.2.jar /usr/lib/spark/jars/
2) inputs required to run streaming application
    i) kafka topic
    ii) kafka broker list
    iii) offset pick : earliest(all history data) or latest (new)
3) Right now, writing data as csv to hdfs sink. you can change it to rds by specifying format as jdbc 