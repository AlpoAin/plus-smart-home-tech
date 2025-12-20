package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;

@RequestMapping("/api/v1/warehouse")
public interface WarehouseApi {

    @PutMapping
    void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/add")
    void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@Valid @RequestBody ShoppingCartDto shoppingCart);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
