package io.equalexperts.model;

import java.math.BigDecimal;
import java.util.Map;

public record ConsolidatedCart(CartError errors, Map<String, ItemMetadata> shoppingCart, CartTotals totals) {

    /**
     * Retrieves the quantity of a product in the cart.
     *
     * @param productName The name of the product to retrieve the quantity for. Must not be null.
     * @return The quantity of the product in the cart. If the product is not in the cart, 0 is returned.
     */
    public int getQuantityForProduct(final String productName) {
        return shoppingCart != null && shoppingCart.containsKey(productName)
                ? shoppingCart.get(productName).getQuantity()
                : 0;
    }

    /**
     * Checks if a product is in the cart.
     *
     * @param productName The name of the product to check for. Must not be null.
     * @return True if the product is in the cart, false otherwise.
     */
    public boolean containsProduct(final String productName) {
        return shoppingCart != null && shoppingCart.containsKey(productName);
    }

    /**
     * Retrieves the price of a product in the cart.
     *
     * @param productName The name of the product to retrieve the price for. Must not be null.
     * @return The price of the product in the cart. If the product is not in the cart, 0 is returned.
     */
    public BigDecimal getPriceForProduct(final String productName) {
        return shoppingCart != null && shoppingCart.containsKey(productName)
                ? shoppingCart.get(productName).getPrice()
                : null;
    }

    /**
     * Retrieves the total number of items in the cart.
     *
     * @return The total number of items in the cart.
     */
    public int getTotalItemsCount() {
        return shoppingCart != null
                ? shoppingCart.values().stream()
                .mapToInt(ItemMetadata::getQuantity)
                .sum()
                : 0;
    }

    /**
     * Retrieves the number of category-items in the cart.
     *
     * @return The number of categorised items in the cart.
     */
    public int getCategorisedItemCount() {
        return shoppingCart != null ? shoppingCart.size() : 0;
    }
}
