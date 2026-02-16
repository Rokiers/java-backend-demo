package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.SkuSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkuSpecificationRepository extends JpaRepository<SkuSpecification, Long> {
    List<SkuSpecification> findBySkuId(Long skuId);
}
