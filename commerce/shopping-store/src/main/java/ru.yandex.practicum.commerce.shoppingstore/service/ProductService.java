package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repo.ProductRepository;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<ProductDto> getProducts(ProductCategory category, int page, int size, List<String> sort) {
        Sort s = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String token : sort) {
                String[] parts = token.split(",");
                String field = parts[0].trim();
                boolean desc = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim());
                s = s.and(desc ? Sort.by(field).descending() : Sort.by(field).ascending());
            }
        }
        return repo.findByProductCategoryAndProductState(category, ProductState.ACTIVE, PageRequest.of(page, size, s))
                .map(this::toDto)
                .toList();
    }

    public ProductDto getProduct(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        if (p.getProductState() == ProductState.DEACTIVATE) {
            throw new ProductNotFoundException(id);
        }
        return toDto(p);
    }

    public ProductDto create(ProductDto dto) {
        Product p = new Product();
        p.setProductId(dto.productId() == null ? UUID.randomUUID() : dto.productId());
        apply(p, dto);
        repo.save(p);
        return toDto(p);
    }

    public ProductDto update(ProductDto dto) {
        if (dto.productId() == null) {
            throw new IllegalArgumentException("productId is required for update");
        }
        Product p = repo.findById(dto.productId()).orElseThrow(() -> new ProductNotFoundException(dto.productId()));
        apply(p, dto);
        repo.save(p);
        return toDto(p);
    }

    public boolean deactivate(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        p.setProductState(ProductState.DEACTIVATE);
        repo.save(p);
        return true;
    }

    public boolean setQuantityState(SetProductQuantityStateRequest req) {
        Product p = repo.findById(req.productId()).orElseThrow(() -> new ProductNotFoundException(req.productId()));
        p.setQuantityState(req.quantityState());
        repo.save(p);
        return true;
    }

    private void apply(Product p, ProductDto dto) {
        p.setProductName(dto.productName());
        p.setDescription(dto.description());
        p.setImageSrc(dto.imageSrc());
        p.setQuantityState(dto.quantityState());
        p.setProductState(dto.productState());
        p.setProductCategory(dto.productCategory());
        p.setPrice(dto.price());
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
