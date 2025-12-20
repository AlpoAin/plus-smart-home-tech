package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repo.ProductRepository;
import ru.yandex.practicum.interaction.api.dto.store.*;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    /**
     * Важно для тестов:
     * - Pageable приходит готовый от Spring (page/size/sort)
     * - возвращаем Page<ProductDto>
     * - фильтруем только ACTIVE
     */
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {

        Page<Product> productPage = (category == null)
                ? repo.findByProductState(ProductState.ACTIVE, pageable)
                : repo.findByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable);

        return productPage.map(this::toDto);
    }

    public ProductDto getProduct(UUID id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return toDto(p);
    }

    public ProductDto create(ProductDto dto) {
        Product p = new Product();
        p.setProductId(dto.productId() == null ? UUID.randomUUID() : dto.productId());

        applyEditableFields(p, dto);

        // дефолты (если пришли null)
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

        applyEditableFields(p, dto);

        // обычно состояние/остаток в update не меняют этим эндпойнтом
        // но если по ТЗ надо — добавишь аналогично create()

        return toDto(repo.save(p));
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

    private void applyEditableFields(Product p, ProductDto dto) {
        if (dto.productName() != null) p.setProductName(dto.productName());
        if (dto.description() != null) p.setDescription(dto.description());
        if (dto.imageSrc() != null) p.setImageSrc(dto.imageSrc());
        if (dto.productCategory() != null) p.setProductCategory(dto.productCategory());
        if (dto.price() != null) p.setPrice(dto.price());
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
