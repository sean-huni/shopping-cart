package io.equalexperts.model;

import java.math.BigDecimal;

public class ItemMetadata {
    private final BigDecimal price;
    private Integer quantity;

    public ItemMetadata(BigDecimal price, Integer quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Method to update quantity
    public void addQuantity(int quantity) {
        if (this.quantity == null) {
            this.quantity = 0;
        }
        this.quantity += quantity;
    }
}
