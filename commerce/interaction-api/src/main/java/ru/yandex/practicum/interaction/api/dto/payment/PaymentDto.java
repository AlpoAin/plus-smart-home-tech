package ru.yandex.practicum.interaction.api.dto.payment;

import java.util.UUID;

public record PaymentDto(
        UUID paymentId,
        Double totalPayment,
        Double deliveryTotal,
        Double feeTotal
) {}