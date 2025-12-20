package ru.yandex.practicum.interaction.api.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        String cause,
        List<StackTraceElement> stackTrace,
        String httpStatus,
        String userMessage,
        String message,
        List<String> suppressed,
        LocalDateTime localizedMessage
) {}
