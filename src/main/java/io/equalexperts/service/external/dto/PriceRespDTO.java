package io.equalexperts.service.external.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Sample Json Response:
 * <p>
 * {
 * "title": "Corn Flakes",
 * "price": 2.52
 * }
 *
 * @param price
 */
public record PriceRespDTO(
        @NotNull(message = "Product Title should not be null")
        @Size(min = 1, max = 100, message = "Price Title must be between {min} and {max} characters")
        String title,
        @NotNull(message = "Product Price should not be null")
        @DecimalMin(value = "0.0", message = "Price must be greater than or equal to {value}")
        BigDecimal price
) {
}
