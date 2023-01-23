#!/usr/bin/env bash

kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic CustomerDetails-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic EnabledCustomer-proto --config cleanup.policy=compact --config retention.ms=600000 --config segment.ms=600000
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersInput-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersOutput-proto
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersInput-avro
kafka-topics --bootstrap-server localhost:9092 --create --partitions 4 --replication-factor 1 --topic OrdersOutput-avro

exit 0;

docker compose up -d
docker compose down
docker compose stop
