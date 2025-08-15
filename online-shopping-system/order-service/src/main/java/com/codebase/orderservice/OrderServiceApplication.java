package com.codebase.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableFeignClients(basePackages = "com.codebase.orderservice.client")
@EnableCassandraRepositories(basePackages = "com.codebase.orderservice.repository")
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
