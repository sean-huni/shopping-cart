package io.equalexperts.component.cart.impl;

import io.equalexperts.component.cart.Cart;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.ItemMetadata;
import io.equalexperts.model.ProductIn;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            items.get(productIn.name()).addQuantity(productIn.quantity());
        } else {
            final var productCart = new ItemMetadata(price, productIn.quantity());
            items.put(productIn.name(), productCart);
        }
        return items;
    }
}
