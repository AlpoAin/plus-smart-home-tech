package ru.yandex.practicum.commerce.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.contract.OrderApi;

@FeignClient(name = "order")
public interface OrderClient extends OrderApi {
}