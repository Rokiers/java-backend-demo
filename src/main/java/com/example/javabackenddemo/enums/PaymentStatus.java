package com.example.javabackenddemo.enums;

import java.util.Map;
import java.util.Set;

public enum PaymentStatus {
    PENDING, SUCCESS, FAILED, REFUNDED;

    private static final Map<PaymentStatus, Set<PaymentStatus>> TRANSITIONS = Map.of(
            PENDING, Set.of(SUCCESS, FAILED),
            SUCCESS, Set.of(REFUNDED),
            FAILED, Set.of(),
            REFUNDED, Set.of()
    );

    public boolean canTransitionTo(PaymentStatus target) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
