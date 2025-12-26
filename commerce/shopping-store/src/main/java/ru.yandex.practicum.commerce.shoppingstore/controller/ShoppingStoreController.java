package ru.yandex.practicum.commerce.shoppingstore.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;
import ru.yandex.practicum.interaction.api.contract.ShoppingStoreApi;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.UUID;

@RestController
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ProductService service;

    public ShoppingStoreController(ProductService service) {
        this.service = service;
    }

    @Override
    public Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    ) {
        return service.getProducts(category, pageable);
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

    @Override
    public boolean setProductQuantityState(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") QuantityState quantityState
    ) {
        return service.setQuantityState(productId, quantityState);
    }

    @Override
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }
}
