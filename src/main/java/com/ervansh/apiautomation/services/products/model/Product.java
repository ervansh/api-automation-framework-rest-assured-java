package com.ervansh.apiautomation.services.products.model;

import java.util.List;

/** DummyJSON ecommerce product representation. */
public record Product(int id, String title, String description, String category, double price,
                      double discountPercentage, double rating, int stock, List<String> tags,
                      String brand, String sku, double weight, Dimensions dimensions,
                      String warrantyInformation, String shippingInformation,
                      String availabilityStatus, List<Review> reviews, String returnPolicy,
                      int minimumOrderQuantity, Meta meta, String thumbnail, List<String> images) {
    public record Dimensions(double width, double height, double depth) {}
    public record Review(int rating, String comment, String date, String reviewerName, String reviewerEmail) {}
    public record Meta(String createdAt, String updatedAt, String barcode, String qrCode) {}
}
