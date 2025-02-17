package io.equalexperts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        @DisplayName("Then getQuantityForProduct returns 0")
        void shouldReturnZeroForGetQuantityForProduct() {
            // Given
            final String productName = "cornflakes";

            // When
            final int quantity = consolidatedCart.getQuantityForProduct(productName);

            // Then
            assertEquals(0, quantity);
        }

        @Test
        @DisplayName("Then getNonExistentQuantityForProduct returns 0")
        void shouldReturnZeroForGetNonExistentQuantityForProduct() {
            // Given
            final var existingItem = Map.of("cheerios", new ItemMetadata(BigDecimal.valueOf(12.34), 3));

            final String productName = "non-existent-product";
            consolidatedCart = new ConsolidatedCart(null, existingItem, null);

            // When
            final int quantity = consolidatedCart.getQuantityForProduct(productName);

            // Then
            assertEquals(0, quantity);
        }

        @Test
        @DisplayName("Then getTotalItemsCount returns 0")
        void shouldReturnZeroForGetTotalItemsCount() {
            // When
            final int totalItemsCount = consolidatedCart.getTotalItemsCount();

            // Then
            assertEquals(0, totalItemsCount);
        }

        @Test
        @DisplayName("Then getCategorisedItemCount returns 0")
        void shouldReturnZeroForGetCategorisedItemCount() {
            // When
            final int categorisedItemCount = consolidatedCart.getCategorisedItemCount();

            // Then
            assertEquals(0, categorisedItemCount);
        }

        @Test
        @DisplayName("Then getPriceForProduct returns NULL")
        void shouldReturnNullForProduct() {
            // Then
            assertNull(consolidatedCart.getPriceForProduct("demo"));
        }

        @Test
        @DisplayName("Then containsProduct Non-Existant Product Return False")
        void shouldReturnFalse() {
            // Given
            final var existingItem = Map.of("conflakes", new ItemMetadata(BigDecimal.valueOf(2.34), 2));

            consolidatedCart = new ConsolidatedCart(null, null, null);
            // When
            final var isAvailable = consolidatedCart.containsProduct("non-existent-product");

            // Then
            assertFalse(isAvailable);
        }

        @Test
        @DisplayName("Then containsProduct Existant Product Return False")
        void shouldReturnExistentProductFalse() {
            // Given
            final var existingItem = Map.of("conflakes", new ItemMetadata(BigDecimal.valueOf(2.34), 2));

            consolidatedCart = new ConsolidatedCart(null, existingItem, null);
            // When
            final var isAvailable = consolidatedCart.containsProduct("conflakes");

            // Then
            assertTrue(isAvailable);
        }

        @Test
        @DisplayName("Then containsProduct Non-Existant Product Return False")
        void shouldNonExistentReturnFalse() {
            // Given
            final var existingItem = Map.of("conflakes", new ItemMetadata(BigDecimal.valueOf(2.34), 2));

            consolidatedCart = new ConsolidatedCart(null, existingItem, null);
            // When
            final var isAvailable = consolidatedCart.containsProduct("non-existent-product");

            // Then
            assertFalse(isAvailable);
        }
    }

    @Nested
    @DisplayName("When ConsolidatedCart is nonNull - Positive Scenarios")
    class WhenConsolidatedCartIsNotNull {

        @Test
        @DisplayName("Then containsProduct returns True")
        void shouldReturnTrue() {
            // Given
            final var existingItem = Map.of("conflakes", new ItemMetadata(BigDecimal.valueOf(2.34), 2));

            consolidatedCart = new ConsolidatedCart(null, existingItem, null);
            // When
            final var isAvailable = consolidatedCart.containsProduct("conflakes");

            // Then
            assertTrue(isAvailable);
        }
    }
}