package com.ervansh.apiautomation.services.products.model;

import java.util.List;

public record ProductListResponse(List<Product> products, int total, int skip, int limit) {}
