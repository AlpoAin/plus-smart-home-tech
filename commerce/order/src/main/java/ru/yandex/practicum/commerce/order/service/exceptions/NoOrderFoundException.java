package ru.yandex.practicum.commerce.order.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }
}