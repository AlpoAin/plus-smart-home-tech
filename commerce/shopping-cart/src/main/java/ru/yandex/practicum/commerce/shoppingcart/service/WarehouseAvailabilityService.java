package ru.yandex.practicum.commerce.shoppingcart.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.shoppingcart.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

@Service
public class WarehouseAvailabilityService {

    private final WarehouseClient warehouseClient;

    public WarehouseAvailabilityService(WarehouseClient warehouseClient) {
        this.warehouseClient = warehouseClient;
    }

    @CircuitBreaker(name = "warehouseClient", fallbackMethod = "warehouseFallback")
    public void check(UUID cartId, Map<UUID, Long> products) {
        warehouseClient.checkProductQuantityEnoughForShoppingCart(new ShoppingCartDto(cartId, products));
    }

    @SuppressWarnings("unused")
    private void warehouseFallback(UUID cartId, Map<UUID, Long> products, Throwable ex) {
        throw new WarehouseUnavailableException("Warehouse is unavailable: " + ex.getMessage());
    }
}
