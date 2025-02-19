package io.equalexperts.service.internal.cartengine;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartError;
import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ProductIn;
import io.equalexperts.model.ProductRm;
import io.equalexperts.service.external.priceclient.PriceApi;
import io.equalexperts.service.internal.CartService;
import io.equalexperts.validators.ValidatorProvider;
import io.equalexperts.validators.wrapper.PriceWrapper;
import io.equalexperts.view.CartSummaryView;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

import static io.equalexperts.constant.ErrorConstants.INTERNAL_ERROR;
import static io.equalexperts.constant.ErrorConstants.NOT_FOUND_ERROR;

@Log4j2
public class CartServiceImpl implements CartService {
    private final PriceApi priceApi;    // For Price validation
    private final Cart cart;
    private final CartCalculator cartCalculator;

    // Rule of Thumb: Max 3 dependencies in class.
    public CartServiceImpl(final PriceApi priceApi, final Cart cart, CartCalculator cartCalculator) {
        this.priceApi = priceApi;
        this.cart = cart;
        this.cartCalculator = cartCalculator;
    }

    @Override
    public CartSummaryView validateAndAddToCart(final ProductIn productIn) {
        try {
            ValidatorProvider.validateData(productIn);    // Validate Client Input Data
            final var price = priceApi.getPrice(productIn.name());    // Get price from Price API
            ValidatorProvider.validateData(new PriceWrapper(price));  // Validate Price Data - Protect CartService from potential API Failures/Bugs.
            final var resp = addToCartAndGetTotals(productIn, price);    // Add product to cart and calculate totals
            return CartSummaryView.from(resp);    // Return the consolidated view of the shopping cart and its totals
        } catch (final CartException e) {
            log.error("CartService Error: {}", e.getMessage(), e);
            final CartError cartError = ValidatorProvider.buildErrors(e);
            final var errResp = new ConsolidatedCart(cartError, null, null);
            return CartSummaryView.from(errResp);
        } catch (Api400xError notFound) {
            log.error("CartService Error: {}", notFound.getMessage(), notFound);
            final CartError cartError = new CartError(notFound.getStatus(), NOT_FOUND_ERROR, null, "Product %s not found".formatted(productIn.name()));
            final var errResp = new ConsolidatedCart(cartError, null, null);
            return CartSummaryView.from(errResp);
        } catch (final Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            final CartError cartError = new CartError(500L, INTERNAL_ERROR, null, "Internal Server Error");
            final var errResp = new ConsolidatedCart(cartError, null, null);
            return CartSummaryView.from(errResp);
        }
    }

    @Override
    public CartSummaryView removeFromCartAndGetTotals(final ProductRm productRm) {
        ValidatorProvider.validateData(productRm);    // Validate Client Input Data
        final var shoppingCart = cart.removeProduct(productRm.name());
        final var totals = cartCalculator.calculateTotals(shoppingCart);
        final var resp = new ConsolidatedCart(null, shoppingCart, totals);
        return CartSummaryView.from(resp);
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
    private ConsolidatedCart addToCartAndGetTotals(final ProductIn productIn, final BigDecimal price) {
        final var shoppingCart = cart.addProduct(productIn, price);
        final var totals = cartCalculator.calculateTotals(shoppingCart);
        return new ConsolidatedCart(null, shoppingCart, totals);
    }
}
