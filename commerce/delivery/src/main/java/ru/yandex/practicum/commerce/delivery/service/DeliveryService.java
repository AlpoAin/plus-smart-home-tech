package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.delivery.client.OrderClient;
import ru.yandex.practicum.commerce.delivery.client.WarehouseClient;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repo.DeliveryRepository;
import ru.yandex.practicum.commerce.delivery.service.exceptions.NoDeliveryFoundException;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryState;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repo;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    private static final double BASE_COST = 5.0;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(Objects.nonNull(dto.deliveryId()) ? dto.deliveryId() : UUID.randomUUID());
        delivery.setOrderId(dto.orderId());

        if (Objects.nonNull(dto.fromAddress())) {
            delivery.setFromCountry(dto.fromAddress().country());
            delivery.setFromCity(dto.fromAddress().city());
            delivery.setFromStreet(dto.fromAddress().street());
            delivery.setFromHouse(dto.fromAddress().house());
            delivery.setFromFlat(dto.fromAddress().flat());
        }

        if (Objects.nonNull(dto.toAddress())) {
            delivery.setToCountry(dto.toAddress().country());
            delivery.setToCity(dto.toAddress().city());
            delivery.setToStreet(dto.toAddress().street());
            delivery.setToHouse(dto.toAddress().house());
            delivery.setToFlat(dto.toAddress().flat());
        }

        delivery.setState(DeliveryState.CREATED);

        repo.save(delivery);
        return toDto(delivery);
    }

    public Double calculateDeliveryCost(OrderDto order) {
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        double cost = BASE_COST;

        String warehouseStreet = warehouseAddress.street();
        if (Objects.nonNull(warehouseStreet)) {
            if (warehouseStreet.contains("ADDRESS_1")) {
                cost *= 1;
            } else if (warehouseStreet.contains("ADDRESS_2")) {
                cost *= 2;
            }
            cost += BASE_COST;
        }

        if (Objects.nonNull(order.fragile()) && order.fragile()) {
            cost += cost * 0.2;
        }

        if (Objects.nonNull(order.deliveryWeight())) {
            cost += order.deliveryWeight() * 0.3;
        }

        if (Objects.nonNull(order.deliveryVolume())) {
            cost += order.deliveryVolume() * 0.2;
        }

        cost += cost * 0.2;

        return cost;
    }

    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = repo.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));

        delivery.setState(DeliveryState.IN_PROGRESS);
        repo.save(delivery);

        orderClient.assembly(delivery.getOrderId());

        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest(
                delivery.getOrderId(),
                delivery.getDeliveryId()
        );
        warehouseClient.shippedToDelivery(request);
    }

    @Transactional
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = repo.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));

        delivery.setState(DeliveryState.DELIVERED);
        repo.save(delivery);

        orderClient.delivery(delivery.getOrderId());
    }

    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = repo.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));

        delivery.setState(DeliveryState.FAILED);
        repo.save(delivery);

        orderClient.deliveryFailed(delivery.getOrderId());
    }

    private DeliveryDto toDto(Delivery d) {
        AddressDto from = new AddressDto(
                d.getFromCountry(),
                d.getFromCity(),
                d.getFromStreet(),
                d.getFromHouse(),
                d.getFromFlat()
        );

        AddressDto to = new AddressDto(
                d.getToCountry(),
                d.getToCity(),
                d.getToStreet(),
                d.getToHouse(),
                d.getToFlat()
        );

        return new DeliveryDto(
                d.getDeliveryId(),
                from,
                to,
                d.getOrderId(),
                d.getState()
        );
    }
}