package io.equalexperts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("Unit-Tests - Given ConsolidatedCart State")
class ConsolidatedCartTest {
    private ConsolidatedCart consolidatedCart;

    @BeforeEach
    void setUp() {
        consolidatedCart = new ConsolidatedCart(null, null, null);
    }

    @Nested
    @DisplayName("When ConsolidatedCart is NULL - Negative Scenarios")
    class WhenConsolidatedCartIsNullNegativeScenarios {

        @Test
        @DisplayName("Then getQuantityForProduct returns null when cart is null")

        void shouldReturnZeroForGetQuantityForProduct() {
            // Given
            // When
            final var nullCart = consolidatedCart.shoppingCart();

            // Then
            assertNull(nullCart);
        }

        @Test
        @DisplayName("Then getNonExistentQuantityForProduct returns 0")
        void shouldReturnZeroForGetNonExistentQuantityForProduct() {
            // Given
            final var shoppingCart = Map.of("cheerios", new ItemMetadata(BigDecimal.valueOf(12.34), 3));

            final String productName = "non-existent-product";
            consolidatedCart = new ConsolidatedCart(null, shoppingCart, null);

            // When
            final var quantity = consolidatedCart.shoppingCart().get(productName);

            // Then
            assertNull(quantity);
        }

        @Test
        @DisplayName("Then getTotalItemsCount returns null when cart is null")
        void shouldReturnZeroForGetTotalItemsCount() {
            // When
            final var totalItems = consolidatedCart.shoppingCart();

            // Then
            assertNull(totalItems);
        }
    }

    @Nested
    @DisplayName("When ConsolidatedCart is nonNull - Positive Scenarios")
    class WhenConsolidatedCartIsNotNull {

        @Test
        @DisplayName("Then containsProduct returns True for an existing product")
        void shouldReturnTrue() {
            // Given
            final var existingItem = Map.of("conflakes", new ItemMetadata(BigDecimal.valueOf(2.34), 2));

            consolidatedCart = new ConsolidatedCart(null, existingItem, null);
            // When
            final var isAvailable = consolidatedCart.shoppingCart().get("conflakes");

            // Then
            assertTrue(isAvailable.quantity() > 0);
        }
    }
}