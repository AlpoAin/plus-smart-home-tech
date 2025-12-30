package ru.yandex.practicum.commerce.order.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException() {
        super("Username must not be blank");
    }

    public NotAuthorizedUserException(String message) {
        super(message);
    }
}