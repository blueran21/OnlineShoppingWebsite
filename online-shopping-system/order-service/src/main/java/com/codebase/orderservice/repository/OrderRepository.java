package com.codebase.orderservice.repository;

import com.codebase.orderservice.model.Order;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cassandra repository for persisting and retrieving orders. By
 * extending {@link CassandraRepository} Spring Data provides
 * asynchronous and synchronous CRUD operations for the Order
 * aggregate.
 */
public interface OrderRepository extends CassandraRepository<Order, UUID> {

    Optional<Order> findByOrderIdAndUserId(UUID orderId, String userId);

    List<Order> findAllByUserId(String userId);
}