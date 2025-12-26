package ru.yandex.practicum.interaction.api.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record OrderDto(
        @NotNull UUID orderId,
        UUID shoppingCartId,
        @NotNull Map<UUID, Long> products,
        UUID paymentId,
        UUID deliveryId,
        OrderState state,
        Double deliveryWeight,
        Double deliveryVolume,
        Boolean fragile,
        Double totalPrice,
        Double deliveryPrice,
        Double productPrice
) {}