package ru.yandex.practicum.commerce.shoppingcart.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class WarehouseUnavailableException extends RuntimeException {
    public WarehouseUnavailableException(String message) {
        super(message);
    }
}
