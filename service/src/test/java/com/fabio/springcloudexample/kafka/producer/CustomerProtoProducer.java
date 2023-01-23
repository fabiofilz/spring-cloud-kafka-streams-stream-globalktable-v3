package com.fabio.springcloudexample.kafka.producer;

import com.fabio.springcloudexample.proto.CustomerDetailsProto;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

import static com.fabio.springcloudexample.kafka.utils.TestUtil.getCustomerProto;

public class CustomerProtoProducer {

  public static void main(String[] args) throws Exception{
    String topicName = "CustomerDetails-proto";
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("clientid", "spring-cloud-kafka-streams-stream-globalktable-protobuf");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
    props.put("schema.registry.url", "http://localhost:8081");
    props.put("auto.register.schemas", "true");

    String customerId = "123456";
    String customerName = "Fabio";
    Boolean enabled = true;
    CustomerDetailsProto.CustomerDetails event = getCustomerProto(customerId, customerName, enabled);

    Producer<String, CustomerDetailsProto.CustomerDetails > producer = new KafkaProducer<>(props);
    producer.send(new ProducerRecord<>(topicName, customerId, event)).get();
    System.out.println("Sent customer to Kafka: " + event);
    producer.flush();
    producer.close();
  }

}
