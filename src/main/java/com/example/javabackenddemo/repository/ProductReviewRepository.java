package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Page<ProductReview> findByProductIdAndVisibleTrue(Long productId, Pageable pageable);
    Page<ProductReview> findByProductId(Long productId, Pageable pageable);
    Optional<ProductReview> findByOrderIdAndUserId(Long orderId, Long userId);
    boolean existsByOrderIdAndUserId(Long orderId, Long userId);
}
