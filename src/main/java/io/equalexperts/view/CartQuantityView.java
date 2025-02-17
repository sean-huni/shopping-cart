package io.equalexperts.view;

import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ItemMetadata;

public record CartQuantityView(int categoryItemCount, int totalItemsCount) {
    public static CartQuantityView from(ConsolidatedCart cart) {
        if (cart.shoppingCart() == null) {
            return new CartQuantityView(0, 0);
        }
        return new CartQuantityView(
                cart.shoppingCart().size(),
                cart.shoppingCart().values().stream()
                        .mapToInt(ItemMetadata::getQuantity)
                        .sum()
        );
    }
}