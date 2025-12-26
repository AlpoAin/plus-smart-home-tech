package ru.yandex.practicum.commerce.delivery.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoDeliveryFoundException extends RuntimeException {
    public NoDeliveryFoundException(UUID deliveryId) {
        super("Delivery not found: " + deliveryId);
    }
}