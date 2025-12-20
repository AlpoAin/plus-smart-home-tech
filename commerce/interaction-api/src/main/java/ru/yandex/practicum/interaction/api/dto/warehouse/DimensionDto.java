package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.Min;

public record DimensionDto(
        @Min(1) double width,
        @Min(1) double height,
        @Min(1) double depth
) {}
