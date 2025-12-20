package ru.yandex.practicum.commerce.shoppingstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.api.exception.ApiErrorResponse;
import ru.yandex.practicum.interaction.api.exception.ProductNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getClass().getName(),
                Arrays.asList(ex.getStackTrace()),
                HttpStatus.NOT_FOUND.name(),
                ex.getUserMessage(),
                ex.getMessage(),
                Collections.emptyList(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
