package com.codebase.orderservice.service;

import com.codebase.orderservice.client.ItemServiceClient;
import com.codebase.orderservice.client.PaymentServiceClient;
import com.codebase.orderservice.dto.OrderItemResponse;
import com.codebase.orderservice.dto.OrderRequest;
import com.codebase.orderservice.dto.OrderResponse;
import com.codebase.orderservice.event.OrderEvent;
import com.codebase.orderservice.gateway.InventoryGateway;
import com.codebase.orderservice.model.Order;
import com.codebase.orderservice.model.OrderItem;
import com.codebase.orderservice.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderAppService {

    private final OrderService domainService;
    private final ItemServiceClient itemClient;
    private final PaymentServiceClient paymentClient;
    private final OrderEventProducer eventProducer;
    private final InventoryGateway inventoryGateway;

    public OrderAppService(OrderService domainService,
                           ItemServiceClient itemClient,
                           PaymentServiceClient paymentClient,
                           OrderEventProducer eventProducer,
                           InventoryGateway inventoryGateway) {
        this.domainService = domainService;
        this.itemClient = itemClient;
        this.paymentClient = paymentClient;
        this.eventProducer = eventProducer;
        this.inventoryGateway = inventoryGateway;
    }

    /** Create Order: use current userId to avoid fake login*/
    public OrderResponse create(OrderRequest req, String userId) {
        // 1) price items
        List<OrderItem> items = req.getItems().stream().map(i -> {
            var dto = itemClient.getItem(i.getItemId());
            double price = dto.price() != null ? dto.price() : 0.0;
            return new OrderItem(i.getItemId(), i.getQuantity(), price);
        }).toList();

        double total = items.stream().mapToDouble(it -> it.getUnitPrice() * it.getQuantity()).sum();

        // 2) decrement inventory
        List<OrderItem> decremented = new ArrayList<>();
        try {
            for (OrderItem it : items) {
                itemClient.decrement(it.getItemId(), it.getQuantity()); // 若不足会抛 409
                decremented.add(it);
            }
        } catch (Exception e) {
            for (OrderItem it : decremented) {
                try { itemClient.increment(it.getItemId(), it.getQuantity()); } catch (Exception ignore) {}
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient inventory");
        }

        // 3) persist order as CREATED（bond with current user）
        Order order = domainService.createOrder(userId, items);

        // 4) publish CREATED event
        eventProducer.sendOrderEvent(new OrderEvent(
                order.getOrderId().toString(),
                order.getUserId(),
                items.stream().map(i -> new OrderEvent.ItemLine(i.getItemId(), i.getQuantity())).toList(),
                total,
                order.getStatus()
        ));

        // 5) call payment
        var payResp = paymentClient.submitPayment(
                new PaymentServiceClient.PaymentRequest(order.getOrderId().toString(), order.getUserId(), total)
        );

        // 6) payment successfully -> marked as PAID
        if (payResp != null && "SUCCESS".equalsIgnoreCase(payResp.status())) {
            domainService.markOrderPaid(order.getOrderId());
            return toResponse(domainService.getOrder(order.getOrderId()).orElse(order));
        }

        // 7) payment failed → roll back inventory and cancel order
        for (OrderItem it : decremented) {
            try { itemClient.increment(it.getItemId(), it.getQuantity()); } catch (Exception ignore) {}
        }
        domainService.cancelOrder(order.getOrderId());
        return toResponse(domainService.getOrder(order.getOrderId()).orElse(order));
    }

    /** Can only be updated with status "Created" and bong with current user. */
    public OrderResponse update(UUID id, OrderRequest req, String userId) {
        var existing = domainService.getOrder(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        ensureOwner(existing, userId);

        if (existing.getStatus() != OrderStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only CREATED orders can be updated");
        }

        List<OrderItem> items = req.getItems().stream().map(i -> {
            var dto = itemClient.getItem(i.getItemId());
            double price = dto.price() != null ? dto.price() : 0.0;
            return new OrderItem(i.getItemId(), i.getQuantity(), price);
        }).collect(Collectors.toList());

        double total = items.stream().mapToDouble(it -> it.getUnitPrice() * it.getQuantity()).sum();
        existing.setItems(items);
        existing.setTotalPrice(total);
        existing.setUpdatedAt(Instant.now());

        existing = domainService.save(existing); // 持久化更新
        return toResponse(existing);
    }

    /** cancel order bond with current user */
    public OrderResponse cancel(UUID id, String userId) {
        var order = domainService.getOrder(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        ensureOwner(order, userId);

        if (order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem line : order.getItems()) {
                inventoryGateway.increment(line.getItemId(), line.getQuantity());
            }
            order = domainService.cancelOrder(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        }
        return toResponse(order);
    }

    /** get order bond with current user */
    public OrderResponse get(UUID id, String userId) {
        var orderOpt = domainService.getOrder(id);
        if (orderOpt.isEmpty()) return null;
        var order = orderOpt.get();
        ensureOwner(order, userId);
        return toResponse(order);
    }

    /** list all order of current user */
    public List<OrderResponse> list(String userId) {
        List<Order> orders = domainService.getOrdersForUser(userId);
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::toResponse).toList();
    }

    private void ensureOwner(Order order, String userId) {
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }
    }

    private OrderResponse toResponse(Order o) {
        var items = o.getItems().stream()
                .map(i -> new OrderItemResponse(i.getItemId(), i.getQuantity(), i.getUnitPrice()))
                .collect(Collectors.toList());
        return new OrderResponse(
                o.getOrderId().toString(),
                o.getUserId(),
                items,
                o.getTotalPrice(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
