package com.example.javabackenddemo.enums;

import java.util.Map;
import java.util.Set;

public enum RefundStatus {
    PENDING, APPROVED, REJECTED, COMPLETED;

    private static final Map<RefundStatus, Set<RefundStatus>> TRANSITIONS = Map.of(
            PENDING, Set.of(APPROVED, REJECTED),
            APPROVED, Set.of(COMPLETED),
            REJECTED, Set.of(),
            COMPLETED, Set.of()
    );

    public boolean canTransitionTo(RefundStatus target) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
