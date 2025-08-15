package com.codebase.orderservice.gateway;


import com.codebase.orderservice.client.ItemServiceClient;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FeignInventoryGateway implements InventoryGateway {

    private final ItemServiceClient client;

    public FeignInventoryGateway(ItemServiceClient client) {
        this.client = client;
    }

    @Override
    public boolean tryDecrement(String itemId, int qty) {
        try {
            client.decrement(itemId, qty); // 2xx 即成功
            return true;
        } catch (FeignException e) {
            // 409 -> 库存不足；404 -> 库存/商品不存在；其它 -> 交给上层处理或记录
            if (e.status() == 409 || e.status() == 404) return false;
            throw e;
        }
    }

    @Override
    public void increment(String itemId, int qty) {
        try {
            client.increment(itemId, qty); // 不关心返回值
        } catch (FeignException e) {
            // 404 代表没有对应库存（严格模式下不应发生），可按需记录告警
            throw e;
        }
    }
}