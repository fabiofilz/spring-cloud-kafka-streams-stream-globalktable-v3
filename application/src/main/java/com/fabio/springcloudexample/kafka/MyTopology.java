package com.fabio.springcloudexample.kafka;


import com.fabio.springcloudexample.proto.EnabledCustomerProto;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Configuration
public class MyTopology {


  /**
   * processEventsProto is responsible for filtering out all events when the client is not enabled
   *
   * @input Param 1 - Event Proto format
   * @input Param 2 - Enabled Customer Proto
   *
   * @return The exit event will be the same as the entry without any modification
   */

  @Bean
  public BiFunction<KStream<String, com.google.protobuf.Message>, GlobalKTable<String, EnabledCustomerProto.EnabledCustomer>, KStream<String, com.google.protobuf.Message>> processProto() {

    return (stream, table) -> (
      stream
        .map((key, value) -> KeyValue.pair(key.trim(), value))
        .peek((key, value) -> log.info("Received event key={} value={}", key, value.toString()))
        .leftJoin(table, (key, value) -> key, (streamRecord, tableRecord) -> {
          if (tableRecord == null) {
            return null;
          }
          return streamRecord;
        })
        .peek((key, value) -> log.info("The customerId={} is enabled={}", key, Optional.ofNullable(value).isPresent())
        )
        .filter((key, value) -> Optional.ofNullable(value).isPresent())
        .peek((key, value) -> log.info("The order from customerId={} will go to the output", key))
    );
  }

  /**
   * processEventsAvro is responsible for filtering out all events when the client is not enabled
   *
   * @input Param 1 - Event Avro format
   * @input Param 2 - Enabled Customer Proto
   *
   * @return The exit event will be the same as the entry without any modification
   */

  @Bean
  public BiFunction<KStream<String, SpecificRecord>, GlobalKTable<String, EnabledCustomerProto.EnabledCustomer>, KStream<String, SpecificRecord>> processAvro() {

    return (stream, table) -> (
      stream
        .map((key, value) -> KeyValue.pair(key.trim(), value))
        .peek((key, value) -> log.info("Received event key={} value={}", key, value.toString()))
        .leftJoin(table, (key, value) -> key, (streamRecord, tableRecord) -> {
          if (tableRecord == null) {
            return null;
          }
          return streamRecord;
        })
        .peek((key, value) -> log.info("The customerId={} is enabled={}", key, Optional.ofNullable(value).isPresent())
        )
        .filter((key, value) -> Optional.ofNullable(value).isPresent())
        .peek((key, value) -> log.info("The order from customerId={} will go to the output", key))
    );
  }


  /**
   * Moves Stream EnabledCustomerProto from Source Kafka cluster to another Kafka Broker.
   *
   * Function's input and output are bound to different kafka clusters in the
   * config file.
   *
   * @return      Function for Spring Cloud Stream to use to sendData data.

   */
  @Bean
  public Function<Message<EnabledCustomerProto.EnabledCustomer>, Message<EnabledCustomerProto.EnabledCustomer>> sendData() {
    return customerUpdated -> {

      String key = Optional.ofNullable(customerUpdated.getHeaders())
        .map(v -> v.get(KafkaHeaders.RECEIVED_MESSAGE_KEY))
        .map(v -> (byte[]) v)
        .map(String::new)
        .get();

      return MessageBuilder
        .withPayload(customerUpdated.getPayload())
        .setHeader(KafkaHeaders.MESSAGE_KEY, key.getBytes(StandardCharsets.UTF_8))
        .setHeader(KafkaHeaders.TIMESTAMP, customerUpdated.getHeaders().get(KafkaHeaders.RECEIVED_TIMESTAMP))
        .setHeader(KafkaHeaders.TIMESTAMP_TYPE, customerUpdated.getHeaders().get(KafkaHeaders.ORIGINAL_TIMESTAMP_TYPE))
        .build();
    };
  }




}
