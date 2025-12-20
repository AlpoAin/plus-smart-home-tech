package ru.yandex.practicum.interaction.api.dto.cart;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record ShoppingCartDto(
        @NotNull UUID shoppingCartId,
        @NotNull Map<UUID, Long> products
) {}
