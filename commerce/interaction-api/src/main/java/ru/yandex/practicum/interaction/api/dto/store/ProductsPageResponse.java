package ru.yandex.practicum.interaction.api.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public record ProductsPageResponse(
        List<ProductDto> items,
        Page page
) {
    @JsonIgnore
    public List<ProductDto> content() { return items; }

    @JsonIgnore
    public List<ProductDto> products() { return items; }

    public ProductsPageResponse(List<ProductDto> items,
                                int number,
                                int size,
                                long totalElements,
                                int totalPages) {
        this(items, new Page(size, number, totalElements, totalPages));
    }

    public record Page(
            int size,
            int number,
            long totalElements,
            int totalPages
    ) { }
}
