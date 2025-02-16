package io.equalexperts.component.calculator;

import io.equalexperts.model.CartTotals;
import io.equalexperts.model.ItemMetadata;

import java.util.Map;

public interface CartCalculator {

    /**
     * Calculates and retrieves the totals for the cart, including tax, subtotal, and total amount
     * based on the provided items and their metadata.
     *
     * @param items A map containing item identifiers as keys and their associated metadata
     *              (price and quantity) as values. Must not be null.
     * @return An instance of CartTotals containing the computed tax, subtotal, and total values for the cart.
     */
    CartTotals calculateTotals(Map<String, ItemMetadata> items);
}