package ru.yandex.practicum.interaction.api.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductsPageResponse(
        @JsonProperty("content") List<ProductDto> content,
        Page page
) {
    // Если хочешь оставить совместимость со старым форматом:
    @JsonProperty("items")
    public List<ProductDto> items() {
        return content;
    }

    // Если вдруг где-то ещё ждут products — можно тоже оставить алиасом:
    @JsonProperty("products")
    public List<ProductDto> products() {
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
