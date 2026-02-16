package com.example.javabackenddemo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sku_specification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SkuSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_id", nullable = false)
    private Long skuId;

    @Column(name = "spec_name", nullable = false, length = 50)
    private String specName;

    @Column(name = "spec_value", nullable = false, length = 100)
    private String specValue;
}
