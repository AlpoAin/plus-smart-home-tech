package ru.yandex.practicum.commerce.shoppingcart.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.contract.WarehouseApi;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseApi {
}
