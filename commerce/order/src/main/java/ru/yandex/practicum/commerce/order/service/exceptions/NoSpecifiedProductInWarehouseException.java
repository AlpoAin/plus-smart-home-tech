package ru.yandex.practicum.commerce.order.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }

    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super("No product in warehouse: " + productId);
    }
}