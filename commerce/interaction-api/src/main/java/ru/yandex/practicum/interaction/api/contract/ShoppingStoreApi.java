package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.UUID;

@RequestMapping(value = ShoppingStoreApi.BASE, produces = MediaType.APPLICATION_JSON_VALUE)
public interface ShoppingStoreApi {

    String BASE = "/api/v1/shopping-store";

    @GetMapping
    Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    );

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") QuantityState quantityState
    );

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
