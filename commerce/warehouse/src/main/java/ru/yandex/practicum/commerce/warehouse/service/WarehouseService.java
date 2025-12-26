package ru.yandex.practicum.commerce.warehouse.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repo.WarehouseProductRepository;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;

import java.security.SecureRandom;
import java.util.*;

@Service
public class WarehouseService {

    private final WarehouseProductRepository repo;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    public WarehouseService(WarehouseProductRepository repo) {
        this.repo = repo;
    }

    public void newProduct(NewProductInWarehouseRequest req) {
        if (repo.existsById(req.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product already exists in warehouse: " + req.productId());
        }
        WarehouseProduct p = new WarehouseProduct();
        p.setProductId(req.productId());
        p.setFragile(req.fragile());
        p.setWidth(req.dimension().width());
        p.setHeight(req.dimension().height());
        p.setDepth(req.dimension().depth());
        p.setWeight(req.weight());
        p.setQuantity(0);
        repo.save(p);
    }

    public void add(AddProductToWarehouseRequest req) {
        WarehouseProduct p = repo.findById(req.productId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("No product in warehouse: " + req.productId()));
        p.setQuantity(p.getQuantity() + req.quantity());
        repo.save(p);
    }

    public BookedProductsDto check(ShoppingCartDto cart) {
        Map<UUID, Long> products = cart.products();
        if (products == null || products.isEmpty()) {
            return new BookedProductsDto(0, 0, false);
        }

        List<String> missing = new ArrayList<>();
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean anyFragile = false;

        for (var e : products.entrySet()) {
            UUID productId = e.getKey();
            long need = e.getValue() == null ? 0 : e.getValue();

            WarehouseProduct p = repo.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("No product in warehouse: " + productId));

            if (need <= 0) {
                missing.add(productId + "(need<=0)");
                continue;
            }
            if (p.getQuantity() < need) {
                missing.add(productId + " need=" + need + " available=" + p.getQuantity());
                continue;
            }

            totalWeight += p.getWeight() * need;
            totalVolume += (p.getWidth() * p.getHeight() * p.getDepth()) * need;
            anyFragile |= p.isFragile();
        }

        if (!missing.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException("Not enough products: " + String.join("; ", missing));
        }

        return new BookedProductsDto(totalWeight, totalVolume, anyFragile);
    }

    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> products = request.products();
        if (products == null || products.isEmpty()) {
            return new BookedProductsDto(0, 0, false);
        }

        List<String> missing = new ArrayList<>();
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean anyFragile = false;

        for (var e : products.entrySet()) {
            UUID productId = e.getKey();
            long need = e.getValue() == null ? 0 : e.getValue();

            WarehouseProduct p = repo.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("No product in warehouse: " + productId));

            if (need <= 0) {
                missing.add(productId + "(need<=0)");
                continue;
            }
            if (p.getQuantity() < need) {
                missing.add(productId + " need=" + need + " available=" + p.getQuantity());
                continue;
            }

            p.setQuantity(p.getQuantity() - need);
            repo.save(p);

            totalWeight += p.getWeight() * need;
            totalVolume += (p.getWidth() * p.getHeight() * p.getDepth()) * need;
            anyFragile |= p.isFragile();
        }

        if (!missing.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException("Not enough products: " + String.join("; ", missing));
        }

        return new BookedProductsDto(totalWeight, totalVolume, anyFragile);
    }

    public void shippedToDelivery(ShippedToDeliveryRequest request) {
    }

    public void acceptReturn(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        for (var e : products.entrySet()) {
            UUID productId = e.getKey();
            long quantity = e.getValue() == null ? 0 : e.getValue();

            if (quantity <= 0) {
                continue;
            }

            WarehouseProduct p = repo.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("No product in warehouse: " + productId));

            p.setQuantity(p.getQuantity() + quantity);
            repo.save(p);
        }
    }

    public AddressDto address() {
        return new AddressDto("KZ", "Almaty", "Abylai Khan", "1", "1");
    }
}
