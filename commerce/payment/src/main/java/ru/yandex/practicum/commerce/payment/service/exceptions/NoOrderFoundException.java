package ru.yandex.practicum.commerce.payment.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(UUID paymentId) {
        super("Payment not found: " + paymentId);
    }
}