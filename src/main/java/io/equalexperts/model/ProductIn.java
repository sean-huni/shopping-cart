package io.equalexperts.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductIn(
        @NotBlank(message = "Product-Name is required")
        @Size(min = 3, max = 50, message = "Product-Name must be between {min} and {max} characters")
        String name,
        @NotNull(message = "Product-Quantity is required")
        @Min(value = 0, message = "Product-Quantity must be zero or more")
        @Digits(integer = 6, fraction = 0, message = "Product-Quantity must be a number with up to 6 digits")
        Integer quantity) {
}
