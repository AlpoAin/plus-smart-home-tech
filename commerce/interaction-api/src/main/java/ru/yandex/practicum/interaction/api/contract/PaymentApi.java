package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;

import java.util.UUID;

@RequestMapping(PaymentApi.BASE)
public interface PaymentApi {

    String BASE = "/api/v1/payment";

    @PostMapping
    PaymentDto payment(@Valid @RequestBody OrderDto order);

    @PostMapping("/totalCost")
    Double getTotalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    Double productCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}