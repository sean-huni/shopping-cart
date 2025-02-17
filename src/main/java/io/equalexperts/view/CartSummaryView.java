package io.equalexperts.view;

import io.equalexperts.model.ConsolidatedCart;

import java.util.List;

public record CartSummaryView(
        CartTotalsView totals,
        CartErrorView errors,
        CartQuantityView quantities,
        List<CartItemView> items
) {
    public static CartSummaryView from(ConsolidatedCart cart) {
        return new CartSummaryView(
                CartTotalsView.from(cart),
                CartErrorView.from(cart),
                CartQuantityView.from(cart),
                CartItemView.fromAll(cart)
        );
    }
}