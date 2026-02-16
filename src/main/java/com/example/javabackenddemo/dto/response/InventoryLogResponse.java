package com.example.javabackenddemo.dto.response;

import java.time.LocalDateTime;

public record InventoryLogResponse(Long id, Integer changeQuantity, Integer afterQuantity,
                                    String changeType, String remark, LocalDateTime createdAt) {}
