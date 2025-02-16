package io.equalexperts.component.tax;

import java.math.BigDecimal;

public interface TaxCalculator {
    BigDecimal calculateTaxAmount(BigDecimal amount);
}
