package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;

public interface WarehouseApi {

    String BASE = "/api/v1/warehouse";

    @PutMapping(BASE)
    void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request);

    @PostMapping(BASE + "/add")
    void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request);

    @PostMapping(BASE + "/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@Valid @RequestBody ShoppingCartDto shoppingCart);

    @GetMapping(BASE + "/address")
    AddressDto getWarehouseAddress();
}
