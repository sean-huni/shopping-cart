package io.equalexperts.model;

import java.math.BigDecimal;

public record CartTotals(BigDecimal tax, BigDecimal subTotal, BigDecimal total) {
}
