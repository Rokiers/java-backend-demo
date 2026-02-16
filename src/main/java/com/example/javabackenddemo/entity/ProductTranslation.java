package com.example.javabackenddemo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_translation", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "language_code"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "language_code", nullable = false, length = 5)
    private String languageCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
