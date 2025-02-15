package io.equalexperts.component.impl;

import io.equalexperts.component.Cart;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartTotals;
import io.equalexperts.model.ItemMetadata;
import io.equalexperts.model.ProductIn;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class CartImpl implements Cart {
    private final Map<String, ItemMetadata> items = new HashMap<>();
    private static final BigDecimal SALE_TAX = BigDecimal.valueOf(12.5);   // 12.5% Tax Rate

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

    /**
     * Calculates and retrieves the totals for the current cart, including tax, subtotal, and total amount.
     * <p>
     * The subtotal is calculated by summing up the price multiplied by the quantity of each item in the cart.
     * The total is derived by adding the applied tax to the subtotal.
     *
     * @return An instance of {@code CartTotals} containing the computed tax, subtotal, and total values for the cart.
     */
    public CartTotals getCartTotals() {
        // Calculate totals
        BigDecimal subTotal = BigDecimal.ZERO;
        // Calculate total price
        for (Map.Entry<String, ItemMetadata> entry : items.entrySet()) {
            final ItemMetadata itemMetadata = entry.getValue();
            subTotal = calculateItemSubTotal(subTotal, itemMetadata.getPrice(), itemMetadata.getQuantity());
        }
        final var taxAmount = calculateTaxAmount(subTotal);
        final var total = subTotal.add(taxAmount);
        log.info("Cart totals: Tax: {}, Subtotal: {}, Total: {}", taxAmount, subTotal, total);  // ToDo: Set to debug/trace level
        return new CartTotals(
                taxAmount.setScale(2, RoundingMode.HALF_UP),
                subTotal.setScale(2, RoundingMode.HALF_UP),
                total.setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Calculates the updated subtotal for an item based on its price and quantity,
     * and adds it to the current subtotal.
     *
     * @param subTotal The current subtotal amount. Must not be null.
     * @param price    The price of the item. Must not be null.
     * @param quantity The quantity of the item. Must be a non-negative integer.
     * @return The updated subtotal as a BigDecimal.
     */
    private BigDecimal calculateItemSubTotal(final BigDecimal subTotal, final BigDecimal price, final int quantity) {
        return subTotal.add(price.multiply(new BigDecimal(quantity)));
    }

    /**
     * Calculates the tax amount based on the provided subtotal and a predefined sales tax rate.
     *
     * @param subTotal The subtotal amount for which the tax is to be calculated. Must not be null.
     * @return The calculated tax amount as a BigDecimal.
     */
    private BigDecimal calculateTaxAmount(final BigDecimal subTotal) {
        return subTotal.multiply(SALE_TAX.divide(BigDecimal.valueOf(100)));
    }
}
