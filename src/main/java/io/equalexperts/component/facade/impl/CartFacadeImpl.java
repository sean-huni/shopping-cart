package io.equalexperts.component.facade.impl;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.facade.CartFacade;
import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ProductIn;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

@Log4j2
public class CartFacadeImpl implements CartFacade {
    private final Cart cart;
    private final CartCalculator cartCalculator;

    public CartFacadeImpl(Cart cart, CartCalculator cartCalculator) {
        this.cart = cart;
        this.cartCalculator = cartCalculator;
    }

    /**
     * Adds a product to the shopping cart, calculates the cart totals, and returns a consolidated view
     * of the shopping cart and its totals. Much like a teller in the physical grocery-store checkout process.
     *
     * @param productIn The product to be added to the cart, including its name and quantity. Must not be null.
     * @param price     The price of the product being added to the cart. Must not be null and must be a non-negative value.
     * @return A ConsolidatedCart object containing potential errors (if any), the updated state of the
     * shopping cart, and the calculated totals including tax, subtotal, and total.
     */
    @Override
    public ConsolidatedCart addToCartAndGetTotals(final ProductIn productIn, final BigDecimal price) {
        final var shoppingCart = cart.addProduct(productIn, price);
        final var totals = cartCalculator.calculateTotals(shoppingCart);
        return new ConsolidatedCart(null, shoppingCart, totals);
    }
}