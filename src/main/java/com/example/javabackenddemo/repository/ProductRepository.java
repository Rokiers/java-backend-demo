package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.Product;
import com.example.javabackenddemo.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatusAndCategoryIdIn(ProductStatus status, List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByKeyword(@Param("status") ProductStatus status, @Param("keyword") String keyword, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE (:name IS NULL OR p.name LIKE %:name%) AND (:categoryId IS NULL OR p.categoryId = :categoryId) AND (:status IS NULL OR p.status = :status)")
    Page<Product> findByFilters(@Param("name") String name, @Param("categoryId") Long categoryId, @Param("status") ProductStatus status, Pageable pageable);
}
