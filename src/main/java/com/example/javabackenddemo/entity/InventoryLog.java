package com.example.javabackenddemo.entity;

import com.example.javabackenddemo.enums.InventoryChangeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "change_quantity", nullable = false)
    private Integer changeQuantity;

    @Column(name = "after_quantity", nullable = false)
    private Integer afterQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private InventoryChangeType changeType;

    @Column(length = 200)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
