package io.equalexperts.component.cart;

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
     * Removes a product from the cart. If the product exists in the cart, it is removed, and the cart's
     * state is updated. If the product does not exist in the cart, no changes are made.
     *
     * @param productName The name of the product to be removed. Must not be null or blank.
     * @return A map representing the updated state of the cart where the keys are product names and
     * the values are ItemMetadata objects containing the price and remaining quantities of the products.
     */
    Map<String, ItemMetadata> removeProduct(final String productName);
}
