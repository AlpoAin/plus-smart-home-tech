package ru.yandex.practicum.interaction.api.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID productId,

        @NotBlank String productName,
        @NotBlank String description,
        String imageSrc,  // nullable по ТЗ

        @NotNull QuantityState quantityState,
        @NotNull ProductState productState,
        ProductCategory productCategory,  // дополнительное поле, не обязательное

        @NotNull @Min(1) BigDecimal price
) {}
