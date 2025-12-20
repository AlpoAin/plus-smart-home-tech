package ru.yandex.practicum.interaction.api.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID productId,

        @NotBlank String productName,
        @NotBlank String description,
        @NotBlank String imageSrc,

        @NotNull QuantityState quantityState,
        @NotNull ProductState productState,
        @NotNull ProductCategory productCategory,

        @NotNull @Positive BigDecimal price
) {}
