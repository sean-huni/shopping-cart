package io.equalexperts.view;

import io.equalexperts.model.ConsolidatedCart;

public record CartErrorView(boolean hasErrors, String errorMessage, String errorType, long statusCode) {
    public static CartErrorView from(ConsolidatedCart cart) {
        if (cart.errors() == null) {
            return new CartErrorView(false, null, null, 0);
        }
        return new CartErrorView(
                true,
                cart.errors().message(),
                cart.errors().errorType(),
                cart.errors().statusCode()
        );
    }
}