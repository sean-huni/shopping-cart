package io.equalexperts.view;

import io.equalexperts.model.ConsolidatedCart;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public record CartItemView(String productName, int quantity, BigDecimal price) {
    public static List<CartItemView> fromAll(ConsolidatedCart cart) {
        if (cart.shoppingCart() == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(cart.shoppingCart().entrySet().stream()
                .map(entry -> new CartItemView(
                        entry.getKey(),
                        entry.getValue().quantity(),
                        entry.getValue().price()))
                .toList());
    }
}