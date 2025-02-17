package io.equalexperts.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@DisplayName("Unit-Tests - Given ItemMetadata")
class ItemMetadataTest {

    @DisplayName("When creating ItemMetadata and adding quantity")
    @Test
    void shouldCreateItemMetadata() {
        // Given
        final var price = 199.09;
        final var quantity = 2;

        // When
        final var itemMetadata = new ItemMetadata(BigDecimal.valueOf(price), null);
        itemMetadata.addQuantity(quantity);

        // Then
        assertEquals(199.09, itemMetadata.getPrice().doubleValue());
        assertEquals(2, itemMetadata.getQuantity());
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
        assertEquals(199.09, itemMetadata.getPrice().doubleValue());
        assertEquals(5, itemMetadata.getQuantity());
    }
}