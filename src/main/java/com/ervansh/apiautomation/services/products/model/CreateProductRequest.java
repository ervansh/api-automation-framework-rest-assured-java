package com.ervansh.apiautomation.services.products.model;

/** Request DTO for DummyJSON's simulated product-creation operation. */
public record CreateProductRequest(String title, String description, String category, double price) {}
