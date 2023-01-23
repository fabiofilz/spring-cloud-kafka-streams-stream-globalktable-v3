
## Start Kafka cluster
```
docker compose -f script/docker-compose.yml up -d
```

## Create topics
```
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic EnabledCustomer-proto --config cleanup.policy=compact --config retention.ms=600000 --config segment.ms=600000
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersInput-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersOutput-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersInput-avro
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersOutput-avro
```

## Open browser to see the topics
```
localhost:8080
```


## Simulate the error => Avro (Parameter 0), Proto (Parameter 1), Avro (output)

1 - Execute application/src/test/java/kafka/producer/CustomerEnabled.java 
It will send a event to the topic EnabledCustomer-proto (GlobalKTable)

2 - Execute application/src/test/java/kafka/producer/OrderAvroProducer.java
It will send a event to the topic OrdersInput-avro that will trigger processAvro function that should send it to the topic OrdersOutput-avro, but it returns an error message


## Simulate the process working => Proto (Parameter 0), Proto (Parameter 1), Proto (output)

1 - Execute application/src/test/java/kafka/producer/CustomerEnabled.java
It will send a event to the topic EnabledCustomer-proto (GlobalKTable)

2 - Execute application/src/test/java/kafka/producer/OrderProtoProducer.java
It will send a event to the topic OrdersInput-proto that will trigger processProto function that will send it to the topic OrdersOutput-proto 

# Stop kafka cluster
```
docker compose -f script/docker-compose.yml down
```
