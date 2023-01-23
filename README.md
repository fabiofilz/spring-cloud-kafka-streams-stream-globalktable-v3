
# Description
The app has 2 parts:

1 - moveData that will read event from one Kafka Cluster and send to another Kafka Cluster 

2 - The Event from step 1 is a GlobalKTable that will be used as filter



2.1 - processOrdersVipCustomersProto will receive a Stream Event as parameter 1 (proto) and execute a left join with GlobalKTable (proto) to remove disabled customers - output proto

2.2 - processOrdersVipCustomersAvro will receive a Stream Event as parameter 1 (avro) and execute a left join with GlobalKTable (proto) to remove disabled customers - output avro

# Steps to simulate

## Start Kafka cluster
```
docker compose -f script/docker-compose.yml up -d
```

## Create topics
```
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic CustomerDetails-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic EnabledCustomer-proto --config cleanup.policy=compact --config retention.ms=600000 --config segment.ms=600000
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersInput-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersOutput-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersInput-avro
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersOutput-avro
```

# Open browser to see the topics
```
localhost:8080
```

# Run App

# Execute the scripts manually
Testing Proto - It works
Execute:
- src/test/java/com/fabio/springcloudexample/kafka/producer/CustomerProtoProducer.java
- src/test/java/com/fabio/springcloudexample/kafka/producer/OrderProtoProducer.java

The input event from OrdersInput-proto will be sent to OrdersOutput-proto

Testing Avro - It is not working
Execute:
- src/test/java/com/fabio/springcloudexample/kafka/producer/OrderAvroProducer.java

The input event from OrdersInput-avro will be sent to OrdersOutput-avro, but it is returning error: Cannot invoke "org.apache.kafka.common.serialization.Serde.serializer()" because "keySerde" is null

# Stop kafka cluster
```
docker compose -f script/docker-compose.yml down
```
