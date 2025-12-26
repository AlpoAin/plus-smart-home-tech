package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NewProductInWarehouseRequest(
        @NotNull UUID productId,
        boolean fragile,
        @NotNull DimensionDto dimension,
        @Min(1) double weight
) {}
