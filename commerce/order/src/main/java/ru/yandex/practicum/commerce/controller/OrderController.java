package ru.yandex.practicum.commerce.order.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.order.service.OrderService;
import ru.yandex.practicum.interaction.api.contract.OrderApi;
import ru.yandex.practicum.interaction.api.dto.order.*;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController implements OrderApi {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Override
    public List<OrderDto> getClientOrders(@RequestParam String username) {
        return service.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        return service.createNewOrder(request);
    }

    @Override
    public OrderDto productReturn(@Valid @RequestBody ProductReturnRequest request) {
        return service.productReturn(request);
    }

    @Override
    public OrderDto payment(@RequestBody UUID orderId) {
        return service.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(@RequestBody UUID orderId) {
        return service.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(@RequestBody UUID orderId) {
        return service.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(@RequestBody UUID orderId) {
        return service.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(@RequestBody UUID orderId) {
        return service.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(@RequestBody UUID orderId) {
        return service.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        return service.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(@RequestBody UUID orderId) {
        return service.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(@RequestBody UUID orderId) {
        return service.assemblyFailed(orderId);
    }
}