package ru.yandex.practicum.commerce.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentState;

import java.util.UUID;

@Data
@Entity
@Table(name = "payments", schema = "payment")
public class Payment {

    @Id
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "total_payment")
    private Double totalPayment;

    @Column(name = "delivery_total")
    private Double deliveryTotal;

    @Column(name = "fee_total")
    private Double feeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentState state;
}