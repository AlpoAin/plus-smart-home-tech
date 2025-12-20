package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;
import ru.yandex.practicum.interaction.api.contract.WarehouseApi;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;

@Validated
@RestController
public class WarehouseController implements WarehouseApi {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @Override
    @PutMapping(WarehouseApi.BASE)
    public void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request) {
        service.newProduct(request);
    }

    @Override
    @PostMapping(WarehouseApi.BASE + "/add")
    public void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) {
        service.add(request);
    }

    @Override
    @PostMapping(WarehouseApi.BASE + "/check")
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(@Valid @RequestBody ShoppingCartDto shoppingCart) {
        return service.check(shoppingCart);
    }

    @Override
    @GetMapping(WarehouseApi.BASE + "/address")
    public AddressDto getWarehouseAddress() {
        return service.address();
    }
}
