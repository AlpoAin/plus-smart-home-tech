package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.UUID;

public interface ShoppingStoreApi {

    String BASE = "/api/v1/shopping-store";

    @GetMapping(BASE)
    Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    );

    @PutMapping(BASE)
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping(BASE)
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping(BASE + "/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping(BASE + "/quantityState")
    boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping(BASE + "/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
