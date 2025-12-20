package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProductToWarehouseRequest(
        @NotNull UUID productId,
        @Min(1) long quantity
) {}
