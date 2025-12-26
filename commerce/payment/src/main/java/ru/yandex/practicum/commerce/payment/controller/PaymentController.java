package ru.yandex.practicum.commerce.payment.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.payment.service.PaymentService;
import ru.yandex.practicum.interaction.api.contract.PaymentApi;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;

import java.util.UUID;

@RestController
public class PaymentController implements PaymentApi {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Override
    public PaymentDto payment(@Valid @RequestBody OrderDto order) {
        return service.createPayment(order);
    }

    @Override
    public Double getTotalCost(@Valid @RequestBody OrderDto order) {
        return service.calculateTotalCost(order);
    }

    @Override
    public void paymentSuccess(@RequestBody UUID paymentId) {
        service.paymentSuccess(paymentId);
    }

    @Override
    public Double productCost(@Valid @RequestBody OrderDto order) {
        return service.calculateProductCost(order);
    }

    @Override
    public void paymentFailed(@RequestBody UUID paymentId) {
        service.paymentFailed(paymentId);
    }
}