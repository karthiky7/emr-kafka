# emr-kafka

author : karthik y

aws managed kafka -- EMR:
1) create MSK cluster in default vpc and with default configuration
2) create EMR Cluster in same vpc of MSK 
3) edit security group of MSK to add EMR security group
4) create ec2 client machine or use EMR client machine 
5) create topic from client machine
    sudo yum install java-1.8.0
	wget https://archive.apache.org/dist/kafka/2.6.2/kafka_2.12-2.6.2.tgz
	tar -xzf kafka_2.12-2.6.2.tgz
	bin/kafka-topics.sh --create --zookeeper ZookeeperConnectString --replication-factor 3 --partitions 1 --topic MSKTutorialTopic  ( get ZookeeperConnectString from MSK page --> client information --> plaintext)
	bin/kafka-topics.sh --create --zookeeper z-1.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:2181,z-2.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:2181,z-3.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:2181 --replication-factor 3 --partitions 1 --topic MSKTutorialTopic
6) produce or consume data from client machine
   cd bin
   security.protocol=PLAINTEXT (create file client.properties with content) 
   ./kafka-console-producer.sh --broker-list BootstrapBrokerString --producer.config client.properties --topic MSKTutorialTopic   (get BootstrapBrokerString from client information --> plain text)
   ./kafka-console-producer.sh --broker-list b-2.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-1.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-3.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092 --producer.config client.properties --topic MSKTutorialTopic
   ./kafka-console-consumer.sh --bootstrap-server b-2.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-1.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092,b-3.demo-cluster-1.25yk5g.c8.kafka.eu-west-1.amazonaws.com:9092 --consumer.config client.properties --topic MSKTutorialTopic --from-beginning
7) open zeppelin notebook from EMR (give 8890 port access to all in security group of EMR master)
    run code by %spark
8) get broker ip's using cli (give msk access to iam user)
	aws kafka describe-cluster --cluster-arn arn:aws:kafka:eu-west-1:209175246806:cluster/demo-cluster-1/a0a95a6c-e547-4422-a628-fe65c8e48666-8
	aws kafka get-bootstrap-brokers --cluster-arn arn:aws:kafka:eu-west-1:209175246806:cluster/demo-cluster-1/a0a95a6c-e547-4422-a628-fe65c8e48666-8
9) add kafka jar
   /home/hadoop/jars/spark-sql-kafka-0-10_2.11-2.4.8.jar
   /home/hadoop/jars/kafka-clients-0.10.2.2.jar
   sudo vim /etc/spark/conf/spark-defaults.conf
      spark.driver.extraClassPath <other existing jar locations>:/home/hadoop/jars/*
      spark.executor.extraClassPath <other existing jar locations>:/home/hadoop/jars/*    
   add below in bootstrap action which will copy s3 jar to all nodes
   sudo aws s3 cp s3://doc-example-bucket/graphframes-0.8.0-spark2.4-s_2.11.jar /usr/lib/spark/jars/
   https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.11/2.4.8/spark-sql-kafka-0-10_2.11-2.4.8.jar
   https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/0.10.2.2/kafka-clients-0.10.2.2.jar
   refer: 1) https://aws.amazon.com/premiumsupport/knowledge-center/emr-permanently-install-library/
10) To give permission to zeppelin user for write access on hdfs
    sudo su hdfs
    hdfs dfs -chown zeppelin /
	
refer https://sparkbyexamples.com/spark/spark-streaming-consume-and-produce-kafka-messages-in-avro-format/ 
https://spark.apache.org/docs/latest/sql-data-sources-avro.html
https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html
