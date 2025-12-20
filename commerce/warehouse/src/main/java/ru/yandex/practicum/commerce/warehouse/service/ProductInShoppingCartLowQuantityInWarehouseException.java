package ru.yandex.practicum.commerce.warehouse.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductInShoppingCartLowQuantityInWarehouseException extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouseException(String message) {
        super(message);
    }
}
