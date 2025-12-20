package ru.yandex.practicum.commerce.shoppingstore.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.shoppingstore.service.ProductService;
import ru.yandex.practicum.interaction.api.contract.ShoppingStoreApi;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ProductService service;

    public ShoppingStoreController(ProductService service) {
        this.service = service;
    }

    @Override
    public List<ProductDto> getProducts(ProductCategory category, int page, int size, List<String> sort) {
        return service.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return service.create(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        return service.update(productDto);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return service.deactivate(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return service.setQuantityState(request);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return service.getProduct(productId);
    }
}
