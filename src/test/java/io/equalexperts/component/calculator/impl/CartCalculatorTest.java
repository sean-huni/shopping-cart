package io.equalexperts.component.calculator.impl;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.model.ItemMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
@DisplayName("Unit-Tests - Given CartCalculator State")
class CartCalculatorTest {
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(16.5));
    private final CartCalculator cartCalculator = new CartCalculatorImpl(taxCalculator);

    @Nested
    @DisplayName("When calculateTotals is called - Positive Scenarios")
    class WhenCalculateTotalsIsCalledPositiveScenarios {

        @Test
        @DisplayName("Then calculate totals for a single item")
        void shouldCalculateTotalsForSingleItem() {
            // Given
            Map<String, ItemMetadata> items = new HashMap<>();
            items.put("cheerios", new ItemMetadata(BigDecimal.valueOf(199.09), 3));

            // When
            var totals = cartCalculator.calculateTotals(items);

            // Then
            assertEquals(98.55, totals.tax().doubleValue());
            assertEquals(597.27, totals.subTotal().doubleValue());
            assertEquals(695.82, totals.total().doubleValue());
        }

        @Test
        @DisplayName("Then calculate totals for multiple items")
        void shouldCalculateTotalsForMultipleItems() {
            // Given
            Map<String, ItemMetadata> items = new HashMap<>();
            items.put("cheerios", new ItemMetadata(BigDecimal.valueOf(19.09), 1));
            items.put("cornflakes", new ItemMetadata(BigDecimal.valueOf(34.99), 5));
            items.put("chocolates", new ItemMetadata(BigDecimal.valueOf(37.79), 15));

            // When
            var totals = cartCalculator.calculateTotals(items);

            // Then
            assertEquals(125.55, totals.tax().doubleValue());
            assertEquals(760.89, totals.subTotal().doubleValue());
            assertEquals(886.44, totals.total().doubleValue());
        }

        @Test
        @DisplayName("Then calculate totals when item quantity is zero")
        void shouldCalculateTotalsWithZeroQuantity() {
            // Given
            Map<String, ItemMetadata> items = new HashMap<>();
            items.put("chocolates", new ItemMetadata(BigDecimal.valueOf(37.79), 1));

            // When
            var totals = cartCalculator.calculateTotals(items);

            // Then
            assertEquals(6.24, totals.tax().doubleValue());
            assertEquals(37.79, totals.subTotal().doubleValue());
            assertEquals(44.03, totals.total().doubleValue());
        }
    }

    @Nested
    @DisplayName("When calculateTotals is called - Special Cases")
    class WhenCalculateTotalsIsCalledSpecialCases {

        @Test
        @DisplayName("Then calculate totals for empty cart")
        void shouldCalculateTotalsForEmptyCart() {
            // Given
            Map<String, ItemMetadata> items = new HashMap<>();

            // When
            var totals = cartCalculator.calculateTotals(items);

            // Then
            assertEquals(0.00, totals.tax().doubleValue());
            assertEquals(0.00, totals.subTotal().doubleValue());
            assertEquals(0.00, totals.total().doubleValue());
        }

        @Test
        @DisplayName("Then calculate totals with price having many decimal places")
        void shouldCalculateTotalsWithLongDecimalPrices() {
            // Given
            final Map<String, ItemMetadata> items = new HashMap<>();
            final var price = BigDecimal.valueOf(99.999999);
            items.put("premium-item", new ItemMetadata(price, 2));

            // When
            var totals = cartCalculator.calculateTotals(items);

            // Then
            assertEquals(33.00, totals.tax().doubleValue());
            assertEquals(200.00, totals.subTotal().doubleValue());
            assertEquals(233.00, totals.total().doubleValue());
        }
    }
}