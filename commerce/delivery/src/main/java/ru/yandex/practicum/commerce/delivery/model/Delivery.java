package ru.yandex.practicum.commerce.delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryState;

import java.util.UUID;

@Data
@Entity
@Table(name = "deliveries", schema = "delivery")
public class Delivery {

    @Id
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "from_country")
    private String fromCountry;

    @Column(name = "from_city")
    private String fromCity;

    @Column(name = "from_street")
    private String fromStreet;

    @Column(name = "from_house")
    private String fromHouse;

    @Column(name = "from_flat")
    private String fromFlat;

    @Column(name = "to_country")
    private String toCountry;

    @Column(name = "to_city")
    private String toCity;

    @Column(name = "to_street")
    private String toStreet;

    @Column(name = "to_house")
    private String toHouse;

    @Column(name = "to_flat")
    private String toFlat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryState state;
}