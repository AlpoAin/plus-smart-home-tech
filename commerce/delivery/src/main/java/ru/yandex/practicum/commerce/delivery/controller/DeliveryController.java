package ru.yandex.practicum.commerce.delivery.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.api.contract.DeliveryApi;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;

import java.util.UUID;

@RestController
public class DeliveryController implements DeliveryApi {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @Override
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto delivery) {
        return service.planDelivery(delivery);
    }

    @Override
    public void deliverySuccessful(@RequestBody UUID deliveryId) {
        service.deliverySuccessful(deliveryId);
    }

    @Override
    public void deliveryPicked(@RequestBody UUID deliveryId) {
        service.deliveryPicked(deliveryId);
    }

    @Override
    public void deliveryFailed(@RequestBody UUID deliveryId) {
        service.deliveryFailed(deliveryId);
    }

    @Override
    public Double deliveryCost(@Valid @RequestBody OrderDto order) {
        return service.calculateDeliveryCost(order);
    }
}