package com.example.javabackenddemo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coupon_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;

    @Column(name = "used_order_id")
    private Long usedOrderId;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @CreationTimestamp
    @Column(name = "claimed_at", nullable = false, updatable = false)
    private LocalDateTime claimedAt;
}
