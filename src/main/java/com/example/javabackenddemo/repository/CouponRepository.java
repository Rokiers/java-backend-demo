package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c.enabled = true AND c.startTime <= :now AND c.endTime >= :now AND c.usedCount < c.totalCount")
    Page<Coupon> findAvailable(LocalDateTime now, Pageable pageable);
}
