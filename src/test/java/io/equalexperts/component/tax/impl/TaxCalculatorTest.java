package io.equalexperts.component.tax.impl;

import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.exception.InvalidTaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@DisplayName("Unit-Tests - Given TaxCalculator")
class TaxCalculatorTest {
    private TaxCalculator taxCalculator;

    @Nested
    @DisplayName("When calculateTaxAmount is called - Positive")
    class whenCalculateTaxAmountIsCalledPositive {
        @Test
        @DisplayName("Then the tax amount should be calculated")
        void shouldCalculateTaxAmount() {
            // Given
            taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(12.5)); // @12.5% tax
            final var subTotal = BigDecimal.valueOf(100);
            // When
            final var taxAmount = taxCalculator.calculateTaxAmount(subTotal);
            // Then
            assertEquals(12.50, taxAmount.doubleValue());
        }
    }

    @Nested
    @DisplayName("When calculateTaxAmount is called - Negative")
    class whenCalculateTaxAmountIsCalledNegative {
        @Test
        @DisplayName("Then the negative tax amount should fail")
        void shouldFailNegativeTaxRate() {
            // Given
            final Exception exc = assertThrows(InvalidTaxException.class, () -> new TaxCalculatorImpl(BigDecimal.valueOf(-12.5))); // @12.5% tax

            // Then
            assertEquals("Tax-rate must be a positive value", exc.getMessage());
        }

        @Test
        @DisplayName("Then the Sub- amount should be calculated")
        void shouldFailNegativeSubtotal() {
            taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(15.599)); // @15.599% tax
            // Given
            final var subTotal = BigDecimal.valueOf(-55);
            // When
            final Exception exc = assertThrows(InvalidTaxException.class, () -> taxCalculator.calculateTaxAmount(subTotal));
            // Then
            assertEquals("Sub-total must be a positive value", exc.getMessage());
        }
    }
}