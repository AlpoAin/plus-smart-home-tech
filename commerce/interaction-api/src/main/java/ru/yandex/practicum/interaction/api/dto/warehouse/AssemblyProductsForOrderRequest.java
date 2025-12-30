package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record AssemblyProductsForOrderRequest(
        @NotNull Map<UUID, Long> products,
        @NotNull UUID orderId
) {}