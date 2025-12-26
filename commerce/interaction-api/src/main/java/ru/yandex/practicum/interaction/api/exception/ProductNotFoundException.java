package ru.yandex.practicum.interaction.api.exception;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private final UUID productId;
    private final String userMessage;
    private final LocalDateTime timestamp;
    private final String httpStatus;

    public ProductNotFoundException(UUID productId) {
        super("Product not found: " + productId);
        this.productId = productId;
        this.userMessage = "Товар с идентификатором " + productId + " не найден";
        this.timestamp = LocalDateTime.now();
        this.httpStatus = "NOT_FOUND";
    }

    public UUID getProductId() {
        return productId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getHttpStatus() {
        return httpStatus;
    }
}
