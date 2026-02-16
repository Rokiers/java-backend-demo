package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.ProductTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductTranslationRepository extends JpaRepository<ProductTranslation, Long> {
    Optional<ProductTranslation> findByProductIdAndLanguageCode(Long productId, String languageCode);
    List<ProductTranslation> findByProductId(Long productId);
}
