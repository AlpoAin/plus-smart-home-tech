package ru.yandex.practicum.interaction.api.dto.store;

import java.util.List;

public record ProductsPageResponse(
        List<ProductDto> products,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
