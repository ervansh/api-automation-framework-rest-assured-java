package com.ervansh.apiautomation.services.products.model;

/**
 * Example product payload used by the sample service client.
 * Replace or extend fields to match the target API's published contract.
 */
public record Product(String id, String name, int priceInCents) {
}
