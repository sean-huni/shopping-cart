package io.equalexperts.model;

import io.equalexperts.exception.InvalidCartParamsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("Unit-Tests - Given ItemMetadata")
class ItemMetadataTest {

    @DisplayName("When creating ItemMetadata and adding NULL quantity")
    @Test
    void shouldNotCreateNullItemMetadataTAndThrowsException() {
        // Given
        final var price = 199.09;
        // When
        final var exception = assertThrows(InvalidCartParamsException.class, () -> new ItemMetadata(BigDecimal.valueOf(price), null));

        // Then
        assertEquals("Quantity must not be null and must be positive integer", exception.getMessage());
    }

    @DisplayName("When creating ItemMetadata and adding quantity")
    @Test
    void shouldCreateItemMetadata() {
        // Given
        final BigDecimal price = null;

        // When
        final var exception = assertThrows(InvalidCartParamsException.class, () -> new ItemMetadata(price, null));

        // Then
        assertTrue(exception.getMessage().contains("Price must not be null and must be non-negative"));
    }

    @DisplayName("When adding negative quantity to ItemMetadata")
    @Test
    void shouldAddNegativeQuantityToItemMetadataThrowsException() {
        // Given
        final var price = 199.09;
        final var quantity = 3;
        final var itemMetadata = new ItemMetadata(BigDecimal.valueOf(price), quantity);

        // When
        final var exception = assertThrows(InvalidCartParamsException.class, () -> itemMetadata.addQuantity(-2));

        // Then
        assertEquals("Quantity must not be null and must be positive integer", exception.getMessage());
    }

    @DisplayName("When adding quantity to ItemMetadata")
    @Test
    void shouldAddQuantityToItemMetadata() {
        // Given
        final var price = 199.09;
        final var quantity = 3;
        final var itemMetadata = new ItemMetadata(BigDecimal.valueOf(price), quantity);

        // When
        itemMetadata.addQuantity(2);

        // Then
        assertEquals(199.09, itemMetadata.price().doubleValue());
        assertEquals(3, itemMetadata.quantity());
    }

    @DisplayName("When adding negative Price to ItemMetadata")
    @Test
    void shouldAddNegativePriceToItemMetadataThrowInvalidCartParamsException() {
        // Given
        final var price = -199.09;
        final var quantity = 3;

        // When
        final var exception = assertThrows(InvalidCartParamsException.class, () -> new ItemMetadata(BigDecimal.valueOf(price), quantity));

        // Then
        assertEquals("Price must not be null and must be non-negative", exception.getMessage());
    }
}