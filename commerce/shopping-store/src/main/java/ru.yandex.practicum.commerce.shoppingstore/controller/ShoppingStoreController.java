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
@RequestMapping(ShoppingStoreApi.BASE)
public class ShoppingStoreController {

    private final ProductService service;

    public ShoppingStoreController(ProductService service) {
        this.service = service;
    }

    /**
     * Важно для тестов:
     * - pageable собирается Spring'ом из page/size/sort
     * - @PageableDefault задаёт дефолтную сортировку, если sort не передан
     * - возвращаем Page<ProductDto> => JSON будет со стандартными полями content/pageable/sort/totalElements...
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ProductDto> getProducts(
            @RequestParam(required = false) ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    ) {
        return service.getProducts(category, pageable);
    }

    @PutMapping
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return service.create(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        return service.update(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return service.deactivate(productId);
    }

    @PostMapping(
            value = "/quantityState",
            params = {"productId", "quantityState"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public boolean setProductQuantityStateFromParams(@RequestParam UUID productId,
                                                     @RequestParam QuantityState quantityState) {
        return service.setQuantityState(new SetProductQuantityStateRequest(productId, quantityState));
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) {
        return service.setQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProduct(productId);
    }
}
