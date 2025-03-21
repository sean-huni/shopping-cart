package io.equalexperts.service.internal;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.calculator.impl.CartCalculatorImpl;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.model.ProductIn;
import io.equalexperts.service.external.priceclient.PriceApi;
import io.equalexperts.service.external.priceclient.impl.PriceApiImpl;
import io.equalexperts.service.internal.cartengine.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.http.HttpClient;

import static io.equalexperts.constant.ErrorConstants.NOT_FOUND_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("int") //Integration test
@DisplayName("Int-Tests - Given CartService")
class CartServiceIntTest {
    private static final String API_URI = "backend-take-home-test-data";
    private static final String BASE_URL = "https://equalexperts.github.io";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(12.5)); // @12.5% tax
    private final PriceApi priceApi = new PriceApiImpl(API_URI, BASE_URL, httpClient);
    private final Cart cart = new CartImpl();
    private final CartCalculator cartCalculator = new CartCalculatorImpl(taxCalculator);
    private final CartService cartService = new CartServiceImpl(priceApi, cart, cartCalculator);

    @Nested
    @DisplayName("Validate & AddToCart - Positive Int-Test Scenarios")
    class ValidateAndAddToCartPositiveScenarios {

        @Test
        @DisplayName("When adding 1 product to cart")
        void thenAdd1ProductToCart() {
            // Given
            final var productIn = new ProductIn("cornflakes", 3);

            // When
            final var cartSummaryView = cartService.validateAndAddToCart(productIn);

            // Then
            assertFalse(cartSummaryView.errors().hasErrors());
            assertNotNull(cartSummaryView.items());
            assertNotNull(cartSummaryView.totals());

            final var cartItem = cartSummaryView.items().stream()
                    .filter(item -> item.productName().equals("cornflakes")).findAny().get();

            final var cartTotals = cartSummaryView.totals();

            assertEquals(1, cartSummaryView.quantities().categoryItemCount());
            assertNotNull(cartItem);
            assertNotNull(cartItem.price());
            assertEquals(3, cartItem.quantity());
            assertEquals("cornflakes", cartItem.productName());
            assertTrue(cartItem.price().compareTo(BigDecimal.ZERO) > 0);  // Suitable Adaptable Int-Test.
            assertEquals(3, cartSummaryView.quantities().totalItemsCount()); //Product Quantity

            // Warning: The following asserted-values are static and will make the tests rigid.
            assertEquals(2.52, cartItem.price().doubleValue());
            assertEquals(7.56, cartTotals.subTotal().doubleValue());
            assertEquals(0.95, cartTotals.tax().doubleValue());
            assertEquals(8.51, cartTotals.total().doubleValue());
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
            final var consolidatedCart = cartService.validateAndAddToCart(new ProductIn("weetabix", 1));
            final var cornflakesCartItem = consolidatedCart.items().stream().filter(item -> item.productName().equals("cornflakes")).findAny();

            // Then
            assertTrue(cornflakesCartItem.isPresent());
            assertFalse(consolidatedCart.errors().hasErrors());
            assertNotNull(consolidatedCart.items());
            assertNotNull(consolidatedCart.totals());
            assertNotNull(cornflakesCartItem.get().price());
            assertEquals("cornflakes", cornflakesCartItem.get().productName());

            assertEquals(5, consolidatedCart.items().size());
            assertEquals(66.1, consolidatedCart.totals().tax().doubleValue());
            assertEquals(528.79, consolidatedCart.totals().subTotal().doubleValue());
            assertEquals(594.89, consolidatedCart.totals().total().doubleValue());
        }
    }

    @Nested
    @DisplayName("Validate & AddToCart - Negative Int-Test Scenarios")
    class ValidateAndAddToCartNegativeScenarios {

        @Test
        @DisplayName("Then fail to add a non-existent product with a negative price")
        void thenAddProductWithNegativePrice() {
            // Given
            final var productIn = new ProductIn("chocolate", 1); //Chocolate does not exist in the Price API

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart.errors());
            assertEquals(0, consolidatedCart.quantities().totalItemsCount());
            assertFalse(consolidatedCart.totals().hasAmount());
            assertEquals(404L, consolidatedCart.errors().statusCode());
            assertTrue(consolidatedCart.errors().hasErrors());
            assertEquals("Product chocolate not found", consolidatedCart.errors().errorMessage());
            assertEquals(NOT_FOUND_ERROR, consolidatedCart.errors().errorType());
        }

        @Test
        @DisplayName("Then fail to add a product with a negative quantity due to validation")
        void thenAddProductWithNegativeQuantity() {
            // Given
            final var productIn = new ProductIn("cornflakes", -1);

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            final var cartTotals = consolidatedCart.totals();

            // Then
            assertNotNull(consolidatedCart.errors());
            assertFalse(cartTotals.hasAmount());
            assertEquals(400L, consolidatedCart.errors().statusCode());
            assertEquals("VALIDATION_ERROR", consolidatedCart.errors().errorMessage());
        }

        @Test
        @DisplayName("Then fail to add a product with an invalid name due to naming constraints")
        void thenAddProductWithInvalidName() {
            // Given
            final var productIn = new ProductIn("invalid-product", 1);

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart.errors());
            assertEquals(0, consolidatedCart.quantities().totalItemsCount());
            assertFalse(consolidatedCart.totals().hasAmount());
            assertEquals(404L, consolidatedCart.errors().statusCode());
            assertEquals(NOT_FOUND_ERROR, consolidatedCart.errors().errorType());
            assertEquals("Product invalid-product not found", consolidatedCart.errors().errorMessage());
        }
    }
}