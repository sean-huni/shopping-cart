package io.equalexperts.component.tax.impl;

import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.exception.InvalidTaxException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxCalculatorImpl implements TaxCalculator {
    private final BigDecimal taxRate;

    public TaxCalculatorImpl(final BigDecimal taxRate) {
        checkForNegativeValues(taxRate, "Tax-rate");
        this.taxRate = taxRate;
    }

    /**
     * Calculates the tax amount based on the provided subtotal and a predefined sales tax rate.
     * <p>
     * It's important to note that rounding-off the sub-total (to 2-decimal place) before calculating the tax amount,
     * may lead to incorrect results. Hence, the sub-total is rounded-off to 10-decimal places after the tax amount is calculated.
     * </p>
     * That way, the accuracy (to approx. 00.01) is still guaranteed, and the final Grand-Total result is rounded-off to 2-decimal places.
     *
     * @param subTotal The subtotal amount for which the tax is to be calculated. Must not be null.
     * @return The calculated tax amount as a BigDecimal.
     */
    public BigDecimal calculateTaxAmount(final BigDecimal subTotal) {
        checkForNegativeValues(subTotal, "Sub-total");
        //only round-off the final result, otherwise the mathematical calculations will be incorrect
        return subTotal.multiply(taxRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                .setScale(10, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    /**
     * Validates that the provided BigDecimal value is not negative. If the value is negative,
     * throws an InvalidTaxException with the specified error attribute.
     *
     * @param value     The BigDecimal value to be checked. Must not be negative.
     * @param attribute The attribute to be included in the exception if the value is negative.
     * @throws InvalidTaxException If the provided value is negative.
     */
    private static void checkForNegativeValues(final BigDecimal value, final String attribute) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTaxException("%s must be a positive value".formatted(attribute));
        }
    }
}