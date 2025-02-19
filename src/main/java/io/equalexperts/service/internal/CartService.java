package io.equalexperts.service.internal;

import io.equalexperts.model.ProductIn;
import io.equalexperts.model.ProductRm;
import io.equalexperts.view.CartSummaryView;

public interface CartService {

    /**
     * Validates the given product information and adds it to the cart.
     * If the validation is successful, the product is added to the cart and the updated cart summary is returned.
     * If validation fails, the cart summary will include the details of any encountered errors.
     *
     * @param productIn the product information, including name and quantity, to be validated and added to the cart
     * @return a {@code CartSummaryView} representing the current state of the cart, including totals, errors, quantities, and items
     */
    CartSummaryView validateAndAddToCart(ProductIn productIn);

    /**
     * Remove a product from the cart and return the updated cart totals
     *
     * @param productRm the product to remove
     * @return the updated cart totals
     */
    CartSummaryView removeFromCartAndGetTotals(final ProductRm productRm);
}
