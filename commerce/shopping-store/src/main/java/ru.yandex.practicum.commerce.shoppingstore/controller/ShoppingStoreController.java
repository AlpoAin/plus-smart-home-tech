package ru.yandex.practicum.commerce.shoppingstore.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;
import ru.yandex.practicum.interaction.api.contract.ShoppingStoreApi;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.UUID;

@RestController
@RequestMapping
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ProductService service;

    public ShoppingStoreController(ProductService service) {
        this.service = service;
    }

    @Override
    @GetMapping(value = BASE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    ) {
        return service.getProducts(category, pageable);
    }

    @Override
    @PutMapping(value = BASE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return service.create(productDto);
    }

    @Override
    @PostMapping(value = BASE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        return service.update(productDto);
    }

    @Override
    @PostMapping(value = BASE + "/removeProductFromStore", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return service.deactivate(productId);
    }

    @Override
    @PostMapping(value = BASE + "/quantityState", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean setProductQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState) {
        return service.setQuantityState(productId, quantityState);
    }

    @Override
    @GetMapping(value = BASE + "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }
}
