package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repo.ProductRepository;
import ru.yandex.practicum.interaction.api.dto.store.*;
import ru.yandex.practicum.interaction.api.exception.ProductNotFoundException;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> productPage = repo.findByProductCategory(category, pageable);
        return productPage.map(this::toDto);
    }

    public ProductDto getProduct(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return toDto(p);
    }

    public ProductDto create(ProductDto dto) {
        Product p = new Product();
        p.setProductId(dto.productId() == null ? UUID.randomUUID() : dto.productId());

        // обязательные поля из dto (по тестам они всегда есть)
        p.setProductName(dto.productName());
        p.setDescription(dto.description());
        p.setImageSrc(dto.imageSrc());
        p.setProductCategory(dto.productCategory());
        p.setPrice(dto.price());

        // состояние
        p.setProductState(dto.productState() != null ? dto.productState() : ProductState.ACTIVE);
        p.setQuantityState(dto.quantityState() != null ? dto.quantityState() : QuantityState.ENDED);

        return toDto(repo.save(p));
    }

    public ProductDto update(ProductDto dto) {
        if (dto.productId() == null) {
            throw new IllegalArgumentException("productId is required for update");
        }

        Product p = repo.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException(dto.productId()));

        // обновляем только изменяемые поля (как в твоём коде)
        if (dto.productName() != null) p.setProductName(dto.productName());
        if (dto.description() != null) p.setDescription(dto.description());
        if (dto.imageSrc() != null) p.setImageSrc(dto.imageSrc());
        if (dto.productCategory() != null) p.setProductCategory(dto.productCategory());
        if (dto.price() != null) p.setPrice(dto.price());

        // по тесту update не проверяет quantityState/productState, но можно поддержать
        if (dto.quantityState() != null) p.setQuantityState(dto.quantityState());
        if (dto.productState() != null) p.setProductState(dto.productState());

        return toDto(repo.save(p));
    }

    public boolean deactivate(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        p.setProductState(ProductState.DEACTIVATE);
        repo.save(p);
        return true;
    }

    @Transactional
    public boolean setQuantityState(UUID productId, QuantityState quantityState) {
        Product p = repo.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        p.setQuantityState(quantityState);
        repo.save(p);
        return true;
    }

    public boolean setQuantityState(SetProductQuantityStateRequest req) {
        return setQuantityState(req.productId(), req.quantityState());
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(
                p.getProductId(),
                p.getProductName(),
                p.getDescription(),
                p.getImageSrc(),
                p.getQuantityState(),
                p.getProductState(),
                p.getProductCategory(),
                p.getPrice()
        );
    }
}
