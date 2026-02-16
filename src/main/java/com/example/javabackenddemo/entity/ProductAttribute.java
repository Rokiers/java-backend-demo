package com.example.javabackenddemo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attribute")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "attr_name", nullable = false, length = 50)
    private String attrName;

    @Column(name = "attr_value", nullable = false, length = 200)
    private String attrValue;
}
