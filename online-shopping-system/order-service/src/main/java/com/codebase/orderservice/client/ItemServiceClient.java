package com.codebase.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "item-service", url = "${item.service.url}")
public interface ItemServiceClient {

    @GetMapping("/items/{id}")
    ItemDto getItem(@PathVariable("id") String id);

    record ItemDto(String id, String name, String upc, Double price) {}

    @PostMapping("/items/{id}/inventory/decrement")
    ResponseEntity<Integer> decrement(@PathVariable("id") String id, @RequestParam("quantity") int qty);

    @PostMapping("/items/{id}/inventory/increment")
    ResponseEntity<Integer> increment(@PathVariable("id") String id, @RequestParam("quantity") int qty);
}
