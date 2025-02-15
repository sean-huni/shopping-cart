package io.equalexperts.validators.wrapper;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record PriceWrapper(
        @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than zero")
        @Digits(integer = 18, fraction = 4, message = "Price must be a valid monetary value (e.g., up to 12 digits before the decimal point and 2 after)")
        BigDecimal price
) {
    public PriceWrapper(BigDecimal price) {
        this.price = price;
    }
}