package ru.yandex.practicum.commerce.shoppingstore.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;
import ru.yandex.practicum.interaction.api.contract.ShoppingStoreApi;
import ru.yandex.practicum.interaction.api.dto.store.*;
import ru.yandex.practicum.interaction.api.dto.store.ProductsPageResponse;


import java.util.List;
import java.util.UUID;

@RestController
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ProductService service;

    public ShoppingStoreController(ProductService service) {
        this.service = service;
    }

    @Override
    public ProductsPageResponse getProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) List<String> sort
    ) {
        return service.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return service.create(productDto);
    }

    @Override
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        return service.update(productDto);
    }

    @Override
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return service.deactivate(productId);
    }

    @PostMapping(
            value = ShoppingStoreApi.BASE + "/quantityState",
            params = {"productId", "quantityState"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public boolean setProductQuantityStateFromParams(@RequestParam UUID productId,
                                                     @RequestParam QuantityState quantityState) {
        return service.setQuantityState(new SetProductQuantityStateRequest(productId, quantityState));
    }

    @Override
    public boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) {
        return service.setQuantityState(request);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return service.getProduct(productId);
    }
}
