package ru.yandex.practicum.commerce.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.order.client.*;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repo.OrderRepository;
import ru.yandex.practicum.commerce.order.service.exceptions.*;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryState;
import ru.yandex.practicum.interaction.api.dto.order.*;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    public OrderService(OrderRepository repo,
                        WarehouseClient warehouseClient,
                        PaymentClient paymentClient,
                        DeliveryClient deliveryClient) {
        this.repo = repo;
        this.warehouseClient = warehouseClient;
        this.paymentClient = paymentClient;
        this.deliveryClient = deliveryClient;
    }

    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException();
        }
        return repo.findByUsername(username).stream().map(this::toDto).toList();
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        // Проверка доступности на складе
        BookedProductsDto booked = warehouseClient.checkProductQuantityEnoughForShoppingCart(request.shoppingCart());

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setShoppingCartId(request.shoppingCart().shoppingCartId());
        order.setProducts(new HashMap<>(request.shoppingCart().products()));
        order.setState(OrderState.NEW);
        order.setDeliveryWeight(booked.deliveryWeight());
        order.setDeliveryVolume(booked.deliveryVolume());
        order.setFragile(booked.fragile());
        order.setUsername("defaultUser"); // Можно передать из контекста

        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findOrder(orderId);

        AssemblyProductsForOrderRequest req = new AssemblyProductsForOrderRequest(
                order.getProducts(), orderId
        );
        warehouseClient.assemblyProductsForOrder(req);

        order.setState(OrderState.ASSEMBLED);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAID);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERED);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrder(orderId);
        Double total = paymentClient.getTotalCost(toDto(order));
        order.setTotalPrice(total);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrder(orderId);
        Double cost = deliveryClient.deliveryCost(toDto(order));
        order.setDeliveryPrice(cost);
        repo.save(order);
        return toDto(order);
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = findOrder(request.orderId());

        warehouseClient.acceptReturn(request.products());

        order.setState(OrderState.PRODUCT_RETURNED);
        repo.save(order);
        return toDto(order);
    }

    private Order findOrder(UUID orderId) {
        return repo.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(orderId));
    }

    private OrderDto toDto(Order o) {
        return new OrderDto(
                o.getOrderId(),
                o.getShoppingCartId(),
                o.getProducts(),
                o.getPaymentId(),
                o.getDeliveryId(),
                o.getState(),
                o.getDeliveryWeight(),
                o.getDeliveryVolume(),
                o.getFragile(),
                o.getTotalPrice(),
                o.getDeliveryPrice(),
                o.getProductPrice()
        );
    }
}