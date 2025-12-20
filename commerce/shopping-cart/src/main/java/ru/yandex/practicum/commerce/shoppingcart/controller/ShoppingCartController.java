package ru.yandex.practicum.commerce.shoppingcart.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.shoppingcart.service.ShoppingCartService;
import ru.yandex.practicum.interaction.api.contract.ShoppingCartApi;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ShoppingCartController implements ShoppingCartApi {

    private final ShoppingCartService service;

    public ShoppingCartController(ShoppingCartService service) {
        this.service = service;
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return service.getOrCreate(username);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        return service.add(username, products);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {
        return service.remove(username, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        return service.changeQuantity(username, request);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        service.deactivate(username);
    }
}
