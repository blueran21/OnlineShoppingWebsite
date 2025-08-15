package com.codebase.orderservice.service;

import com.codebase.orderservice.model.Order;
import com.codebase.orderservice.model.OrderItem;
import com.codebase.orderservice.model.OrderStatus;
import com.codebase.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /** Create a new order. Start in CREATED state. */
    public Order createOrder(String userId, List<OrderItem> items) {
        UUID orderId = UUID.randomUUID();
        Instant now = Instant.now();
        double totalPrice = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        Order order = new Order(orderId, userId, items, totalPrice, OrderStatus.CREATED, now, now);
        return orderRepository.save(order);
    }

    /** Cancel an existing order. */
    public Optional<Order> cancelOrder(UUID orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /** Mark an existing order as PAID. */
    public Optional<Order> markOrderPaid(UUID orderId) {
        return updateOrderStatus(orderId, OrderStatus.PAID);
    }

    /** Complete an order. */
    public Optional<Order> completeOrder(UUID orderId) {
        return updateOrderStatus(orderId, OrderStatus.COMPLETED);
    }

    /** Lookup a single order by ID. */
    public Optional<Order> getOrder(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    /** List all orders. */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /** Save updated order (items/total/updatedAt ...). */
    public Order save(Order order) {
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    /** Get the specific order from current user */
    public Optional<Order> getOrderForUser(UUID orderId, String userId) {
        try {
            return orderRepository.findByOrderIdAndUserId(orderId, userId);
        } catch (Throwable ignore) {
            return getOrder(orderId).filter(o -> userId.equals(o.getUserId()));
        }
    }

    /** Get all orders of current user */
    public List<Order> getOrdersForUser(String userId) {
        try {
            return orderRepository.findAllByUserId(userId);
        } catch (Throwable ignore) {
            return getAllOrders().stream()
                    .filter(o -> userId.equals(o.getUserId()))
                    .toList();
        }
    }

    /** Update order status helper. */
    private Optional<Order> updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(newStatus);
            order.setUpdatedAt(Instant.now());
            return Optional.of(orderRepository.save(order));
        }
        return Optional.empty();
    }
}
