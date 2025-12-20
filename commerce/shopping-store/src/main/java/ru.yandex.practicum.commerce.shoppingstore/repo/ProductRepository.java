package ru.yandex.practicum.commerce.shoppingstore.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductState;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategory(ProductCategory category, Pageable pageable);

    Page<Product> findByProductState(ProductState productState, Pageable pageable);

    Page<Product> findByProductCategoryAndProductState(ProductCategory category,
                                                       ProductState productState,
                                                       Pageable pageable);
}
