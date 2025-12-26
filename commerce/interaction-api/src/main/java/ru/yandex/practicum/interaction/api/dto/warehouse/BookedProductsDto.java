package ru.yandex.practicum.interaction.api.dto.warehouse;

public record BookedProductsDto(
        double deliveryWeight,
        double deliveryVolume,
        boolean fragile
) {}
