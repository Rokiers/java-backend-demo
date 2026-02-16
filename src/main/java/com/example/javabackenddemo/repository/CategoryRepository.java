package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIdIsNull();
    List<Category> findByParentId(Long parentId);
    Optional<Category> findByNameAndParentId(String name, Long parentId);
    boolean existsByNameAndParentId(String name, Long parentId);
}
