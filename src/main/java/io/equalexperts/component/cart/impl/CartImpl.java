package io.equalexperts.component.cart.impl;

import io.equalexperts.component.cart.Cart;
import io.equalexperts.exception.CartException;
import io.equalexperts.exception.InvalidProductRemovalException;
import io.equalexperts.model.ItemMetadata;
import io.equalexperts.model.ProductIn;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Log4j2
public class CartImpl implements Cart {
    private final Map<String, ItemMetadata> items = new ConcurrentHashMap<>();

    /**
     * Adds a product to the cart with the specified price. If the product already exists in the cart,
     * its quantity is incremented by the provided quantity. Otherwise, a new product is added to the cart.
     *
     * @param productIn The product information including its name and quantity. Must not be null and must contain valid values.
     * @param price     The price of the product to be added. Must not be null and must represent a non-negative value.
     * @return A map representing the current state of the cart, where the keys are product names and
     * the values are {@code ItemMetadata} objects containing the price and the updated quantity of products.
     */
    public Map<String, ItemMetadata> addProduct(final ProductIn productIn, final BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new CartException("Price must be non-negative");
        }
        // Add product to cart
        if (items.containsKey(productIn.name())) {
            final ItemMetadata updatedItemMetadata = items.get(productIn.name()).addQuantity(productIn.quantity());
            items.put(productIn.name(), updatedItemMetadata);
            return Collections.unmodifiableMap(items);
        } else {
            final var productCart = new ItemMetadata(price, productIn.quantity());
            items.put(productIn.name(), productCart);
        }
        return Collections.unmodifiableMap(items);
    }

    /**
     * Removes a product from the cart. If the product exists in the cart, it is removed, and the cart's
     * state is updated. If the product does not exist in the cart, no changes are made.
     *
     * @param productName The name of the product to be removed. Must not be null or blank.
     * @return A map representing the updated state of the cart where the keys are product names and
     * the values are ItemMetadata objects containing the price and remaining quantities of the products.
     */
    @Override
    public Map<String, ItemMetadata> removeProduct(final String productName) {
        if (isNull(productName) || productName.isBlank()) {
            throw new InvalidProductRemovalException("Product name must not be null or blank");
        }
        items.remove(productName);
        return Collections.unmodifiableMap(items);
    }
}
