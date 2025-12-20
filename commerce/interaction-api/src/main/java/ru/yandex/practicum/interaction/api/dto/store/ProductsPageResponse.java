package ru.yandex.practicum.interaction.api.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductsPageResponse(
        @JsonProperty("products") List<ProductDto> products,
        Page page
) {
    @JsonProperty("items")
    public List<ProductDto> items() {
        return products;
    }

    public ProductsPageResponse(List<ProductDto> products,
                                int number,
                                int size,
                                long totalElements,
                                int totalPages) {
        this(products, new Page(size, number, totalElements, totalPages));
    }

    public record Page(int size, int number, long totalElements, int totalPages) { }
}
