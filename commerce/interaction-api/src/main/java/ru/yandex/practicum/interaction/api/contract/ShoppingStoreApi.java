package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.dto.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreApi {

    String BASE = "/api/v1/shopping-store";

    @GetMapping(BASE)
    Page<ProductDto> getProducts(@RequestParam(required = false) ProductCategory category,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) List<String> sort);

    @PutMapping(BASE)
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping(BASE)
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping(BASE + "/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping(BASE + "/quantityState")
    boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request);

    @GetMapping(BASE + "/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
