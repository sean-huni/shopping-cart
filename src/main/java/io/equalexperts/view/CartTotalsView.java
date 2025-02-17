package io.equalexperts.view;

import io.equalexperts.model.ConsolidatedCart;

import java.math.BigDecimal;

public record CartTotalsView(
        BigDecimal tax,
        BigDecimal subTotal,
        BigDecimal total,
        boolean hasAmount
) {
    public static CartTotalsView from(ConsolidatedCart cart) {
        if (cart.totals() == null) {
            return new CartTotalsView(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    false
            );
        }
        return new CartTotalsView(
                cart.totals().tax(),
                cart.totals().subTotal(),
                cart.totals().total(),
                true
        );
    }
}