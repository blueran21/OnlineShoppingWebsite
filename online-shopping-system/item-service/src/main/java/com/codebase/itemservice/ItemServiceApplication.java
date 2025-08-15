package com.codebase.itemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Item Service. This service manages product metadata
 * such as the item name, description, pictures and price. It also
 * exposes operations for checking and updating inventory levels. The
 * service uses MongoDB as its datastore for the flexible document
 * structure required to model items and inventory.
 */
@SpringBootApplication
@EnableFeignClients
public class ItemServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }
}
