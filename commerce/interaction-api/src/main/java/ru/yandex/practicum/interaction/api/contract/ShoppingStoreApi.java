package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.dto.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-store")
public interface ShoppingStoreApi {

    @GetMapping
    List<ProductDto> getProducts(@RequestParam ProductCategory category,
                                 @RequestParam int page,
                                 @RequestParam int size,
                                 @RequestParam(required = false) List<String> sort);

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
