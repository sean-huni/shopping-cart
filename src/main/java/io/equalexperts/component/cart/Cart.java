package io.equalexperts.component.cart;

import io.equalexperts.model.CartTotals;
import io.equalexperts.model.ItemMetadata;
import io.equalexperts.model.ProductIn;

import java.math.BigDecimal;
import java.util.Map;

public interface Cart {

    /**
     * Adds a product to the cart with the specified price. If the product already exists in the cart,
     * its quantity is updated. Otherwise, the product is added to the cart.
     *
     * @param productIn The product information including name and quantity. Must not be null.
     * @param price     The price of the product. Must not be null and must be a non-negative value.
     * @return A map representing the current state of the cart where the keys are product names and
     * the values are ItemMetadata objects containing price and updated quantities of products.
     */
    Map<String, ItemMetadata> addProduct(final ProductIn productIn, final BigDecimal price);

    /**
     * Calculates and retrieves the totals for the cart, including tax, subtotal, and total amount.
     *
     * @return An instance of CartTotals containing the computed tax, subtotal, and total values for the cart.
     */
    CartTotals getCartTotals();
}
