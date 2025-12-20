package ru.yandex.practicum.commerce.shoppingcart.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "carts", schema = "shopping_cart")
public class ShoppingCart {

    @Id
    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartState state;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "cart_products",
            schema = "shopping_cart",
            joinColumns = @JoinColumn(name = "cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    private Map<UUID, Long> products = new HashMap<>();

    public UUID getCartId() { return cartId; }
    public void setCartId(UUID cartId) { this.cartId = cartId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public CartState getState() { return state; }
    public void setState(CartState state) { this.state = state; }
    public Map<UUID, Long> getProducts() { return products; }
    public void setProducts(Map<UUID, Long> products) { this.products = products; }
}
