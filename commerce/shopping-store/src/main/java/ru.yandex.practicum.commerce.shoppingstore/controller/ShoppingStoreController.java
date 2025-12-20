package ru.yandex.practicum.commerce.shoppingstore.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;
import ru.yandex.practicum.interaction.api.contract.ShoppingStoreApi;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ShoppingStoreApi.BASE)
public class ShoppingStoreController {

    private final ProductService service;

    public ShoppingStoreController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ProductsPageResponse getProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sort
    ) {
        return service.getProducts(category, page, size, sort);
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

    // если тесты требуют вариант с query params — оставляем
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
//