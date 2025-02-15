package io.equalexperts.model;

import java.util.Map;

public record ConsolidatedCart(CartError errors, Map<String, ItemMetadata> shoppingCart, CartTotals totals) {
}
