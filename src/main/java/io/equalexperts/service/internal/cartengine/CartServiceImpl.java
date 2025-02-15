package io.equalexperts.service.internal.cartengine;

import io.equalexperts.component.Cart;
import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartError;
import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ProductIn;
import io.equalexperts.service.external.priceclient.PriceAPIClient;
import io.equalexperts.service.internal.CartService;
import io.equalexperts.validators.ValidatorProvider;
import io.equalexperts.validators.wrapper.PriceWrapper;
import lombok.extern.log4j.Log4j2;

import static io.equalexperts.constant.ErrorConstants.INTERNAL_ERROR;
import static io.equalexperts.constant.ErrorConstants.NOT_FOUND_ERROR;

@Log4j2
public class CartServiceImpl implements CartService {
    private final ValidatorProvider validator;    // For Product validation
    private final PriceAPIClient priceAPIClient;    // For Price validation
    private final Cart cart;      // For Cart operations

    // I like the Rule of Thumb: Max 3 dependencies in constructor.
    public CartServiceImpl(final ValidatorProvider validator, final PriceAPIClient priceAPIClient, final Cart cart) {
        this.validator = validator;
        this.priceAPIClient = priceAPIClient;
        this.cart = cart;
    }

    @Override
    public ConsolidatedCart validateAndAddToCart(final ProductIn productIn) {
        try {
            validator.validateData(productIn);    // Validate Client Input Data

            final var price = priceAPIClient.getPrice(productIn.name());    // Get price from Price API

            validator.validateData(new PriceWrapper(price));  // Validate Price Data - Protect CartService from potential API Failures/Bugs.

            final var shoppingCart = cart.addProduct(productIn, price);
            final var totals = cart.getCartTotals();
            return new ConsolidatedCart(null, shoppingCart, totals);
        } catch (final CartException e) {
            log.error("CartService Error: {}", e.getMessage(), e);
            final CartError cartError = validator.buildErrors(e);
            return new ConsolidatedCart(cartError, null, null);
        } catch (Api400xError notFound) {
            log.error("CartService Error: {}", notFound.getMessage(), notFound);
            final CartError cartError = new CartError(notFound.getStatus(), NOT_FOUND_ERROR, null, "Product %s not found".formatted(productIn.name()));
            return new ConsolidatedCart(cartError, null, null);
        } catch (final Exception e) {
            log.error("CartException: {}", e.getMessage(), e);
            final CartError cartError = new CartError(500L, INTERNAL_ERROR, null, "Internal Server Error");
            return new ConsolidatedCart(cartError, null, null);
        }
    }
}
