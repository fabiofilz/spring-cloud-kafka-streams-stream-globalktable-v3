package com.fabio.springcloudexample.kafka;


import com.fabio.springcloudexample.proto.EnabledCustomerProto;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.function.BiFunction;

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

}
