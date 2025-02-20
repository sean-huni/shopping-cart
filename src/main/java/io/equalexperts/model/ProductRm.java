package io.equalexperts.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRm(
        @NotBlank(message = "Product-Name is required")
        @Size(min = 3, max = 50, message = "Product-Name must be between {min} and {max} characters")
        String name) {
}
