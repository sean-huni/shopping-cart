package io.equalexperts.service.internal.cartengine;

import io.equalexperts.component.facade.CartFacade;
import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartError;
import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ProductIn;
import io.equalexperts.service.external.priceclient.PriceAPIClient;
import io.equalexperts.service.internal.CartService;
import io.equalexperts.validators.ValidatorProvider;
import io.equalexperts.validators.wrapper.PriceWrapper;
import io.equalexperts.view.CartSummaryView;
import lombok.extern.log4j.Log4j2;

import static io.equalexperts.constant.ErrorConstants.INTERNAL_ERROR;
import static io.equalexperts.constant.ErrorConstants.NOT_FOUND_ERROR;

@Log4j2
public class CartServiceImpl implements CartService {
    private final ValidatorProvider validator;    // For Product validation
    private final PriceAPIClient priceAPIClient;    // For Price validation
    private final CartFacade cartFacade;      // For Cart operations

    // Rule of Thumb: Max 3 dependencies in class.
    public CartServiceImpl(final ValidatorProvider validator, final PriceAPIClient priceAPIClient, final CartFacade cartFacade) {
        this.validator = validator;
        this.priceAPIClient = priceAPIClient;
        this.cartFacade = cartFacade;
    }

    @Override
    public CartSummaryView validateAndAddToCart(final ProductIn productIn) {
        try {
            validator.validateData(productIn);    // Validate Client Input Data
            final var price = priceAPIClient.getPrice(productIn.name());    // Get price from Price API
            validator.validateData(new PriceWrapper(price));  // Validate Price Data - Protect CartService from potential API Failures/Bugs.
            final var resp = cartFacade.addToCartAndGetTotals(productIn, price);    // Add product to cart and calculate totals
            return CartSummaryView.from(resp);    // Return the consolidated view of the shopping cart and its totals
        } catch (final CartException e) {
            log.error("CartService Error: {}", e.getMessage(), e);
            final CartError cartError = validator.buildErrors(e);
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
}
