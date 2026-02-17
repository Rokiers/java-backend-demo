package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);
    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);
}
