package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;
import ru.yandex.practicum.interaction.api.contract.WarehouseApi;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class WarehouseController implements WarehouseApi {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @Override
    public void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request) {
        service.newProduct(request);
    }

    @Override
    public void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) {
        service.add(request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(@Valid @RequestBody ShoppingCartDto shoppingCart) {
        return service.check(shoppingCart);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return service.address();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(@Valid @RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }

    @Override
    public void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        service.shippedToDelivery(request);
    }

    @Override
    public void acceptReturn(@RequestBody Map<UUID, Long> products) {
        service.acceptReturn(products);
    }
}