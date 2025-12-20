package ru.yandex.practicum.interaction.api.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductsPageResponse(
        List<ProductDto> content,
        Page page
) {
    @JsonProperty("products")
    public List<ProductDto> products() { return content; }

    // если нужно сохранить старое имя тоже:
    @JsonProperty("items")
    public List<ProductDto> items() {
        return content;
    }

    public ProductsPageResponse(List<ProductDto> content,
                                int number,
                                int size,
                                long totalElements,
                                int totalPages) {
        this(content, new Page(size, number, totalElements, totalPages));
    }

    public record Page(int size, int number, long totalElements, int totalPages) { }
}
