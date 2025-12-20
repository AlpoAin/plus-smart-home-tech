package ru.yandex.practicum.interaction.api.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductsPageResponse(
        List<ProductDto> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    // алиас как у Spring Page: content[]
    @JsonProperty("content")
    public List<ProductDto> content() {
        return items;
    }

    // иногда тесты читают products[]
    @JsonProperty("products")
    public List<ProductDto> products() {
        return items;
    }

    // алиас номера страницы как у Spring Page: number
    @JsonProperty("number")
    public int number() {
        return page;
    }
}
