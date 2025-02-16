package io.equalexperts.component.tax;

import java.math.BigDecimal;

public interface TaxCalculator {
    /**
     * Calculates the tax amount based on the provided subtotal and a predefined sales tax rate.
     *
     * @param subTotal The subtotal amount for which the tax is to be calculated. Must not be null.
     * @return The calculated tax amount as a BigDecimal.
     */
    BigDecimal calculateTaxAmount(BigDecimal subTotal);
}
