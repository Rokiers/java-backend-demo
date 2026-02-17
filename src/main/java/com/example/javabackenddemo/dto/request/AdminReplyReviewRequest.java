package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminReplyReviewRequest(
        @NotBlank String reply
) {}
