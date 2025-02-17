package io.equalexperts.component.facade;

import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ProductIn;

import java.math.BigDecimal;

public interface CartFacade {

    /**
     * Adds a product to the shopping cart, calculates the cart totals, and returns a consolidated view
     * of the shopping cart and its totals. Much like a teller in the physical grocery-store checkout process.
     *
     * @param productIn The product to be added to the cart, including its name and quantity. Must not be null.
     * @param price     The price of the product being added to the cart. Must not be null and must be a non-negative value.
     * @return A ConsolidatedCart object containing potential errors (if any), the updated state of the
     * shopping cart, and the calculated totals including tax, subtotal, and total.
     */
    ConsolidatedCart addToCartAndGetTotals(ProductIn productIn, BigDecimal price);
}