package ru.yandex.practicum.interaction.api.dto.store;

import java.util.List;

public record ProductsPageResponse(
        List<ProductDto> content,
        List<ProductDto> products,
        Page page
) {
    public ProductsPageResponse(List<ProductDto> items,
                                int number,
                                int size,
                                long totalElements,
                                int totalPages) {
        this(items, items, new Page(size, number, totalElements, totalPages));
    }

    public record Page(
            int size,
            int number,
            long totalElements,
            int totalPages
    ) { }
}
