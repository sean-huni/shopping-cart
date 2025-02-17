package io.equalexperts.service.internal;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.calculator.impl.CartCalculatorImpl;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.component.facade.CartFacade;
import io.equalexperts.component.facade.impl.CartFacadeImpl;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.model.ProductIn;
import io.equalexperts.service.external.priceclient.PriceAPIClient;
import io.equalexperts.service.external.priceclient.impl.PriceAPIClientImpl;
import io.equalexperts.service.internal.cartengine.CartServiceImpl;
import io.equalexperts.validators.ValidatorProvider;
import io.equalexperts.validators.impl.ValidatorProviderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.equalexperts.constant.ErrorConstants.NOT_FOUND_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("int") //Integration test
@DisplayName("Int-Tests - Given CartService")
class CartServiceIntTest {
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(12.5)); // @12.5% tax
    private final ValidatorProvider validatorProvider = new ValidatorProviderImpl();
    private final PriceAPIClient priceAPIClient = new PriceAPIClientImpl();
    private final Cart cart = new CartImpl();
    private final CartCalculator cartCalculator = new CartCalculatorImpl(taxCalculator);
    private final CartFacade cartFacade = new CartFacadeImpl(cart, cartCalculator);
    private final CartService cartService = new CartServiceImpl(validatorProvider, priceAPIClient, cartFacade);

    @Nested
    @DisplayName("Validate & AddToCart - Positive Int-Test Scenarios")
    class ValidateAndAddToCartPositiveScenarios {

        @Test
        @DisplayName("When adding 1 product to cart")
        void thenAdd1ProductToCart() {
            // Given
            final var productIn = new ProductIn("cornflakes", 3);

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNull(consolidatedCart.errors());
            assertNotNull(consolidatedCart.shoppingCart());
            assertNotNull(consolidatedCart.totals());

            assertEquals(1, consolidatedCart.getCategorisedItemCount());
            assertNotNull(consolidatedCart.getPriceForProduct("cornflakes"));
            assertTrue(consolidatedCart.containsProduct("cornflakes"));
            assertEquals(1, consolidatedCart.getPriceForProduct("cornflakes").compareTo(BigDecimal.valueOf(0)));  // Suitable Adaptable Int-Test.
            assertEquals(3, consolidatedCart.getQuantityForProduct("cornflakes"));

            // Warning: The following asserted-values are static and will make the tests rigid.
            assertEquals(2.52, consolidatedCart.getPriceForProduct("cornflakes").doubleValue());
            assertEquals(7.56, consolidatedCart.totals().subTotal().doubleValue());
            assertEquals(0.95, consolidatedCart.totals().tax().doubleValue());
            assertEquals(8.51, consolidatedCart.totals().total().doubleValue());
        }

        @Test
        @DisplayName("When adding 2 products to cart")
        void thenAdd5ProductToCart() {
            // Given
            cartService.validateAndAddToCart(new ProductIn("cheerios", 10));
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("frosties", 45));
            cartService.validateAndAddToCart(new ProductIn("shreddies", 23));
            cartService.validateAndAddToCart(new ProductIn("weetabix", 10));

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(new ProductIn("weetabix", 0));

            // Then
            assertNull(consolidatedCart.errors());
            assertNotNull(consolidatedCart.shoppingCart());
            assertNotNull(consolidatedCart.totals());
            assertNotNull(consolidatedCart.getPriceForProduct("cornflakes"));
            assertTrue(consolidatedCart.containsProduct("cornflakes"));

            assertEquals(5, consolidatedCart.getCategorisedItemCount());
            assertEquals(64.85, consolidatedCart.totals().tax().doubleValue());
            assertEquals(518.81, consolidatedCart.totals().subTotal().doubleValue());
            assertEquals(583.66, consolidatedCart.totals().total().doubleValue());
        }
    }

    @Nested
    @DisplayName("Validate & AddToCart - Negative Int-Test Scenarios")
    class ValidateAndAddToCartNegativeScenarios {

        @Test
        @DisplayName("When adding a non-existent product with negative")
        void thenAddProductWithNegativePrice() {
            // Given
            final var productIn = new ProductIn("chocolate", 1); //Chocolate does not exist in the Price API

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart.errors());
            assertNull(consolidatedCart.shoppingCart());
            assertNull(consolidatedCart.totals());
            assertNull(consolidatedCart.shoppingCart());
            assertEquals(404L, consolidatedCart.errors().statusCode());
            assertEquals("Product chocolate not found", consolidatedCart.errors().message());
            assertEquals(NOT_FOUND_ERROR, consolidatedCart.errors().errorType());
        }

        @Test
        @DisplayName("When adding a product with negative quantity")
        void thenAddProductWithNegativeQuantity() {
            // Given
            final var productIn = new ProductIn("cornflakes", -1);

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart.errors());
            assertNull(consolidatedCart.shoppingCart());
            assertNull(consolidatedCart.totals());
            assertEquals(400L, consolidatedCart.errors().statusCode());
            assertEquals("VALIDATION_ERROR", consolidatedCart.errors().message());
        }

        @Test
        @DisplayName("When adding a product with invalid name")
        void thenAddProductWithInvalidName() {
            // Given
            final var productIn = new ProductIn("invalid-product", 1);

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart.errors());
            assertNull(consolidatedCart.shoppingCart());
            assertNull(consolidatedCart.totals());
            assertEquals(404L, consolidatedCart.errors().statusCode());
            assertEquals(NOT_FOUND_ERROR, consolidatedCart.errors().errorType());
            assertEquals("Product invalid-product not found", consolidatedCart.errors().message());
        }
    }
}