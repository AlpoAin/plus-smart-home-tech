package ru.yandex.practicum.interaction.api.dto.warehouse;

public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {}
