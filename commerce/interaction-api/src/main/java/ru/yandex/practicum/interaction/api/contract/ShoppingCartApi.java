package ru.yandex.practicum.interaction.api.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartApi {

    String BASE = "/api/v1/shopping-cart";

    @GetMapping(BASE)
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping(BASE)
    ShoppingCartDto addProductToShoppingCart(@RequestParam String username,
                                             @RequestBody Map<UUID, Long> products);

    @PostMapping(BASE + "/remove")
    ShoppingCartDto removeFromShoppingCart(@RequestParam String username,
                                           @RequestBody List<UUID> products);

    @PostMapping(BASE + "/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                          @Valid @RequestBody ChangeProductQuantityRequest request);

    @DeleteMapping(BASE)
    void deactivateCurrentShoppingCart(@RequestParam String username);
}
