package ru.yandex.practicum.interaction.api.dto.delivery;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;

import java.util.UUID;

public record DeliveryDto(
        @NotNull UUID deliveryId,
        @NotNull AddressDto fromAddress,
        @NotNull AddressDto toAddress,
        @NotNull UUID orderId,
        @NotNull DeliveryState deliveryState
) {}