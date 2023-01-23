package com.fabio.springcloudexample.kafka;


import com.fabio.springcloudexample.proto.CustomerDetailsProto;
import com.fabio.springcloudexample.proto.EnabledCustomerProto;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
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
   * processOrdersVipCustomersProto is responsible for filtering out all events when the client is not enabled and VIP
   *
   * @input Param 1 - Order Proto
   * @input Param 2 - Enabled Customer Proto
   *
   * @return The exit event will be the same as the entry without any modification
   */

  @Bean
  public BiFunction<KStream<String, com.google.protobuf.Message>, GlobalKTable<String, EnabledCustomerProto.EnabledCustomer>, KStream<String, com.google.protobuf.Message>> processOrdersVipCustomersProto() {
    return processEventsProto();
  }

  /**
   * processOrdersVipCustomersAvro is responsible for filtering out all events when the client is not enabled and VIP
   *
   * @input Param 1 - Order Avro
   * @input Param 2 - Enabled Customer Proto
   *
   * @return The exit event will be the same as the entry without any modification
   */

  @Bean
  public BiFunction<KStream<String, SpecificRecord>, GlobalKTable<String, EnabledCustomerProto.EnabledCustomer>, KStream<String, SpecificRecord>> processOrdersVipCustomersAvro() {
    return processEventsAvro();
  }

  /**
   * processEventsProto is responsible for filtering out all events when the client is not enabled
   *
   * @input Param 1 - Event Proto format
   * @input Param 2 - Enabled Customer Proto
   *
   * @return The exit event will be the same as the entry without any modification
   */

  @Bean
  public BiFunction<KStream<String, com.google.protobuf.Message>, GlobalKTable<String, EnabledCustomerProto.EnabledCustomer>, KStream<String, com.google.protobuf.Message>> processEventsProto() {

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
  public BiFunction<KStream<String, SpecificRecord>, GlobalKTable<String, EnabledCustomerProto.EnabledCustomer>, KStream<String, SpecificRecord>> processEventsAvro() {

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
   * Moves Stream CustomerDetailsProto from Cluster A to KTABLE/Compact topic in Cluster B.
   * Function is responsible creating tombstone event when customer is disabled
   * <p>
   * Function's input and output are bound to different kafka clusters in the
   * config file.
   *
   * @return Function for Spring Cloud Stream to use to move data.
   */
  @Bean
  public Function<Message<CustomerDetailsProto.CustomerDetails>, Message<?>> moveData() {
    return customerDetails -> {

      String key = Optional.ofNullable(customerDetails.getHeaders())
        .map(v -> v.get(KafkaHeaders.RECEIVED_MESSAGE_KEY))
        .map(v -> (byte[]) v)
        .map(String::new)
        .get();

      Boolean customerEnabled = Optional.ofNullable(customerDetails.getPayload().getEnabled())
        .orElse(false);

      //  tombstone
      var payLoad = customerEnabled ?
        EnabledCustomerProto.EnabledCustomer.newBuilder().setCustomerId(key).build() :
        KafkaNull.INSTANCE;

      log.info("moveData key={} value={}", (String) key, payLoad);

      return MessageBuilder
        .withPayload(payLoad)
        .setHeader(KafkaHeaders.MESSAGE_KEY, key.getBytes(StandardCharsets.UTF_8))
        .setHeader(KafkaHeaders.TIMESTAMP, customerDetails.getHeaders().get(KafkaHeaders.RECEIVED_TIMESTAMP))
        .setHeader(KafkaHeaders.TIMESTAMP_TYPE, customerDetails.getHeaders().get(KafkaHeaders.ORIGINAL_TIMESTAMP_TYPE))
        .build();
    };
  }
}
