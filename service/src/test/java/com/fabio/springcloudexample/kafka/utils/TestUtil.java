package com.fabio.springcloudexample.kafka.utils;

import com.fabio.springcloudexample.avro.OrdersAvro;
import com.fabio.springcloudexample.proto.CustomerDetailsProto;
import com.fabio.springcloudexample.proto.OrdersProto;
import com.github.javafaker.Faker;

public class TestUtil {

    public static final Faker FAKER = new Faker();

  public static OrdersProto.Orders getOrderProto(String customerId, String item, Integer qty ) {

    return OrdersProto.Orders.newBuilder()
      .setCustomerId(customerId)
      .setItem(item)
      .setQty(qty)
      .build();
  }

  public static CustomerDetailsProto.CustomerDetails getCustomerProto(String customerId, String name, Boolean enable ) {

    return CustomerDetailsProto.CustomerDetails.newBuilder()
      .setCustomerId(customerId)
      .setName(name)
      .setEnabled(enable)
      .build();
  }

  public static OrdersAvro getOrderAvro(String customerId, String item, Integer qty ) {

    return OrdersAvro.newBuilder()
      .setCustomerId(customerId)
      .setItem(item)
      .setQty(qty)
      .build();
  }

}
