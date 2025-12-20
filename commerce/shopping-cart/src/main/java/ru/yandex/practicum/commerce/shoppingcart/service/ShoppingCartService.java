package ru.yandex.practicum.commerce.shoppingcart.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.shoppingcart.client.WarehouseClient;
import ru.yandex.practicum.commerce.shoppingcart.model.CartState;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.commerce.shoppingcart.repo.ShoppingCartRepository;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.*;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository repo;
    private final WarehouseClient warehouseClient;

    public ShoppingCartService(ShoppingCartRepository repo, WarehouseClient warehouseClient) {
        this.repo = repo;
        this.warehouseClient = warehouseClient;
    }

    public ShoppingCartDto getOrCreate(String username) {
        validateUsername(username);
        ShoppingCart cart = repo.findByUsernameAndState(username, CartState.ACTIVE)
                .orElseGet(() -> {
                    ShoppingCart c = new ShoppingCart();
                    c.setCartId(UUID.randomUUID());
                    c.setUsername(username);
                    c.setState(CartState.ACTIVE);
                    c.setProducts(new HashMap<>());
                    return repo.save(c);
                });
        return toDto(cart);
    }

    public ShoppingCartDto deactivate(String username) {
        validateUsername(username);
        repo.findByUsernameAndState(username, CartState.ACTIVE).ifPresent(c -> {
            c.setState(CartState.DEACTIVATED);
            repo.save(c);
        });
        // по спецификации delete возвращает просто 200 OK, dto не надо
        return null;
    }

    public ShoppingCartDto remove(String username, List<UUID> productIds) {
        validateUsername(username);
        ShoppingCart cart = getActiveCart(username);

        boolean removedAny = false;
        for (UUID id : productIds) {
            if (cart.getProducts().remove(id) != null) {
                removedAny = true;
            }
        }
        if (!removedAny) {
            throw new NoProductsInShoppingCartException("No requested products in shopping cart");
        }
        repo.save(cart);
        return toDto(cart);
    }

    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest req) {
        validateUsername(username);
        ShoppingCart cart = getActiveCart(username);

        if (!cart.getProducts().containsKey(req.productId())) {
            throw new NoProductsInShoppingCartException("Product not found in shopping cart: " + req.productId());
        }
        cart.getProducts().put(req.productId(), req.newQuantity());

        // проверка склада на итоговую корзину
        checkWarehouseOrThrow(cart);

        repo.save(cart);
        return toDto(cart);
    }

    public ShoppingCartDto add(String username, Map<UUID, Long> toAdd) {
        validateUsername(username);
        ShoppingCart cart = repo.findByUsernameAndState(username, CartState.ACTIVE)
                .orElseGet(() -> {
                    ShoppingCart c = new ShoppingCart();
                    c.setCartId(UUID.randomUUID());
                    c.setUsername(username);
                    c.setState(CartState.ACTIVE);
                    c.setProducts(new HashMap<>());
                    return repo.save(c);
                });

        if (cart.getState() != CartState.ACTIVE) {
            throw new ShoppingCartDeactivatedException();
        }

        // формируем итоговую карту количества
        Map<UUID, Long> merged = new HashMap<>(cart.getProducts());
        for (var e : toAdd.entrySet()) {
            UUID productId = e.getKey();
            long q = e.getValue() == null ? 0 : e.getValue();
            if (q <= 0) throw new IllegalArgumentException("Quantity must be >= 1 for product " + productId);
            merged.merge(productId, q, Long::sum);
        }

        // проверяем склад ДО сохранения изменений
        checkWarehouseOrThrow(cart.getCartId(), merged);

        cart.setProducts(merged);
        repo.save(cart);
        return toDto(cart);
    }

    private ShoppingCart getActiveCart(String username) {
        ShoppingCart cart = repo.findByUsernameAndState(username, CartState.ACTIVE)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Active shopping cart not found"));
        if (cart.getState() != CartState.ACTIVE) {
            throw new ShoppingCartDeactivatedException();
        }
        return cart;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException();
        }
    }

    private ShoppingCartDto toDto(ShoppingCart cart) {
        return new ShoppingCartDto(cart.getCartId(), Collections.unmodifiableMap(cart.getProducts()));
    }

    private void checkWarehouseOrThrow(ShoppingCart cart) {
        checkWarehouseOrThrow(cart.getCartId(), cart.getProducts());
    }

    @CircuitBreaker(name = "warehouseClient", fallbackMethod = "warehouseFallback")
    private void checkWarehouseOrThrow(UUID cartId, Map<UUID, Long> products) {
        warehouseClient.checkProductQuantityEnoughForShoppingCart(new ShoppingCartDto(cartId, products));
    }

    @SuppressWarnings("unused")
    private void warehouseFallback(UUID cartId, Map<UUID, Long> products, Throwable ex) {
        throw new WarehouseUnavailableException("Warehouse is unavailable: " + ex.getMessage());
    }
}
