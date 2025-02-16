package io.equalexperts.component.calculator.impl;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.math.BigDecimal;

@Tag("unit")
@DisplayName("Unit-Tests - Given CartCalculator State")
class CartCalculatorTest {
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(16.5));
    private final CartCalculator cartCalculator = new CartCalculatorImpl(taxCalculator);


}