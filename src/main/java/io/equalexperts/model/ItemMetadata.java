package io.equalexperts.model;

import io.equalexperts.exception.InvalidCartParamsException;

import java.math.BigDecimal;

public record ItemMetadata(BigDecimal price, Integer quantity) {
    public ItemMetadata {
        validateData(price, quantity);
    }

    // Method to update quantity
    public ItemMetadata addQuantity(int quantity) {
        validateData(this.price, quantity);
        int newQuantity = this.quantity;
        newQuantity += quantity;
        return new ItemMetadata(this.price, newQuantity);
    }

    private void validateData(final BigDecimal price, final Integer quantity) {
        if (price == null || price.doubleValue() < 0) {
            throw new InvalidCartParamsException("Price must not be null and must be non-negative");
        }

        if (quantity == null || quantity <= 0) {
            throw new InvalidCartParamsException("Quantity must not be null and must be positive integer");
        }
    }
}
