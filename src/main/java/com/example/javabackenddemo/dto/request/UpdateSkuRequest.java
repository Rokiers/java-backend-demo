package com.example.javabackenddemo.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateSkuRequest(BigDecimal price, List<SpecificationRequest> specifications) {}
