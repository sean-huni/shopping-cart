package io.equalexperts.component.calculator.impl;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.model.CartTotals;
import io.equalexperts.model.ItemMetadata;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Log4j2
public class CartCalculatorImpl implements CartCalculator {
    private final TaxCalculator taxCalculator;

    public CartCalculatorImpl(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }

    /**
     * Calculates and retrieves the totals for the cart, including tax, subtotal, and total amount
     * based on the provided items and their metadata.
     *
     * @param items A map containing item identifiers as keys and their associated metadata
     *              (price and quantity) as values. Must not be null.
     * @return An instance of CartTotals containing the computed tax, subtotal, and total values for the cart.
     */
    @Override
    public CartTotals calculateTotals(final Map<String, ItemMetadata> items) {
        // Calculate totals
        BigDecimal subTotal = BigDecimal.ZERO;
        // Calculate total price
        for (Map.Entry<String, ItemMetadata> entry : items.entrySet()) {
            final ItemMetadata itemMetadata = entry.getValue();
            subTotal = calculateItemSubTotal(subTotal, itemMetadata.getPrice(), itemMetadata.getQuantity());
        }

        final var taxAmount = taxCalculator.calculateTaxAmount(subTotal);
        final var total = subTotal.add(taxAmount);
        log.debug("Cart totals: Tax: {}, Subtotal: {}, Total: {}", taxAmount, subTotal, total);
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
}
