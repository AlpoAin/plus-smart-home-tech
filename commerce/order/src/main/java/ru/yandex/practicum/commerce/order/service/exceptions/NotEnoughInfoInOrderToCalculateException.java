package ru.yandex.practicum.commerce.order.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    public NotEnoughInfoInOrderToCalculateException(String message) {
        super(message);
    }

    public NotEnoughInfoInOrderToCalculateException() {
        super("Not enough information in order to calculate");
    }
}