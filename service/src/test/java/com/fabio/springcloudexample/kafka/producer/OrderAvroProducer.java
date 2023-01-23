package com.fabio.springcloudexample.kafka.producer;

import com.fabio.springcloudexample.avro.OrdersAvro;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

import static com.fabio.springcloudexample.kafka.utils.TestUtil.getOrderAvro;

public class OrderAvroProducer {

      public static void main(String[] args) throws Exception{
        String topicName = "OrdersInput-avro";
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("clientid", "spring-cloud-kafka-streams-stream-globalktable-avro");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");
        props.put("auto.register.schemas", "true");

        String customerId = "123456";
        String item = "box";
        Integer qty = 1;
        OrdersAvro event = getOrderAvro(customerId, item, qty);

        Producer<String, OrdersAvro> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>(topicName, customerId, event)).get();
        System.out.println("Sent Order avro to Kafka: \n" + event);
        producer.flush();
        producer.close();
    }

}
