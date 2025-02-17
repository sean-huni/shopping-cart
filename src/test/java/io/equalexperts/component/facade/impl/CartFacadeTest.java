package io.equalexperts.component.facade.impl;

import io.equalexperts.component.calculator.impl.CartCalculatorImpl;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.component.facade.CartFacade;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.model.ProductIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@DisplayName("Unit-Tests - Given CartFacade State")
class CartFacadeTest {
    private final Cart cart = new CartImpl();
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(12.5)); // @12.5% tax
    private CartFacade cartFacade;

    @BeforeEach
    void setUp() {
        cartFacade = new CartFacadeImpl(cart, new CartCalculatorImpl(taxCalculator));
    }

    @Nested
    @DisplayName("When checkoutAndShowTotals is called - Positive Scenarios")
    class WhenCheckoutAndShowTotalsIsCalledPositiveScenarios {

        @Test
        @DisplayName("Then calculate totals of Cart")
        void shouldAddProductToCartAndCalculateTotals() {
            // Given
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(2.52));
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 6), BigDecimal.valueOf(2.52));
            cartFacade.addToCartAndGetTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(9.98));
            cartFacade.addToCartAndGetTotals(new ProductIn("cheerios", 3), BigDecimal.valueOf(1.33));

            final var productIn = new ProductIn("frosties", 3);
            final var price = BigDecimal.valueOf(3.09);

            // When
            final var consolidatedCart = cartFacade.addToCartAndGetTotals(productIn, price);

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var totals = consolidatedCart.totals();

            assertNotNull(totals);
            assertNull(consolidatedCart.errors());
            assertEquals(7.61, totals.tax().doubleValue());
            assertEquals(60.84, totals.subTotal().doubleValue());
            assertEquals(68.45, totals.total().doubleValue());
        }

        @Test
        @DisplayName("Then calculate totals of Cart with all items @100% Discount")
        void shouldAddProductToCartWith100PercentDiscountAndCalculateTotals() {
            // Given
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 6), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("cheerios", 3), BigDecimal.valueOf(00.00));

            final var productIn = new ProductIn("frosties", 3);
            final var price = BigDecimal.valueOf(00.00);

            // When
            final var consolidatedCart = cartFacade.addToCartAndGetTotals(productIn, price);

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var totals = consolidatedCart.totals();

            assertNotNull(totals);
            assertNull(consolidatedCart.errors());
            assertEquals(00.00, totals.tax().doubleValue());
            assertEquals(00.00, totals.subTotal().doubleValue());
            assertEquals(00.00, totals.total().doubleValue());
        }

        @Test
        @DisplayName("Then verify items in Cart")
        void shouldAddProductToCartAndVerifyItemsInCart() {
            // Given
            final var productIn = new ProductIn("cheerios", 3);
            final var price = BigDecimal.valueOf(10.09);
            // When
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(2.52));
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(2.52));
            cartFacade.addToCartAndGetTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(9.98));
            cartFacade.addToCartAndGetTotals(new ProductIn("shreddies", 1), BigDecimal.valueOf(17.23));
            cartFacade.addToCartAndGetTotals(new ProductIn("frosties", 1), BigDecimal.valueOf(4.11));
            final var consolidatedCart = cartFacade.addToCartAndGetTotals(productIn, price);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var itemsInCart = consolidatedCart.shoppingCart();

            assertNotNull(itemsInCart);
            assertNull(consolidatedCart.errors());
            assertEquals(5, consolidatedCart.getCategorisedItemCount());    // Categories of items in cart
            assertTrue(consolidatedCart.containsProduct("cornflakes")); // Frequency of Product in Cart
            assertEquals(2, consolidatedCart.getQuantityForProduct("cornflakes")); // Frequency of Product in Cart
            assertEquals(10, consolidatedCart.getTotalItemsCount()); // Total items in cart
        }

        @Test
        @DisplayName("Then verify all items in Cart @100% Discount")
        void shouldAddProductToCartAndVerifyItemsWith100PercentDiscountInCart() {
            // Given
            final var productIn = new ProductIn("cheerios", 3);
            final var price = BigDecimal.valueOf(00.00);
            // When
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("shreddies", 1), BigDecimal.valueOf(00.00));
            cartFacade.addToCartAndGetTotals(new ProductIn("frosties", 1), BigDecimal.valueOf(00.00));
            final var consolidatedCart = cartFacade.addToCartAndGetTotals(productIn, price);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var itemsInCart = consolidatedCart.shoppingCart();

            assertNotNull(itemsInCart);
            assertNull(consolidatedCart.errors());
            assertEquals(5, consolidatedCart.getCategorisedItemCount());    // Categories of items in cart
            assertEquals(2, consolidatedCart.getQuantityForProduct("cornflakes")); // Frequency of Product in Cart
            assertEquals(10, consolidatedCart.getTotalItemsCount()); // Total items in cart
        }
    }

    @Nested
    @DisplayName("When checkoutAndShowTotals is called - Negative Scenarios")
    class WhenCheckoutAndShowTotalsIsCalledNegativeScenarios {

        @Test
        @DisplayName("Then calculate totals of Cart with invalid Product")
        void shouldAddProductToCartWithInvalidProductAndCalculateTotals() {
            // Given
            final var productIn = new ProductIn("cornflakes", 1);
            final var price = BigDecimal.valueOf(2.52);
            // When
            final var consolidatedCart = cartFacade.addToCartAndGetTotals(productIn, price);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());
            assertNull(consolidatedCart.errors());
            assertNull(consolidatedCart.getPriceForProduct("non-existent"));
        }

        @Test
        @DisplayName("Then calculate totals of Cart with invalid Product and Price")
        void shouldAddProductToCartWithInvalidProductAndPriceAndCalculateTotals() {
            // Given
            final var productIn = new ProductIn("cornflakes", 1);
            final var price = BigDecimal.valueOf(0.00);
            // When
            final var consolidatedCart = cartFacade.addToCartAndGetTotals(productIn, price);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());
            assertNull(consolidatedCart.errors());
            assertEquals(0.00, consolidatedCart.getPriceForProduct("cornflakes").doubleValue());
            assertEquals(1, consolidatedCart.getQuantityForProduct("cornflakes"));
            assertEquals(1, consolidatedCart.getCategorisedItemCount());
            assertEquals(1, consolidatedCart.getTotalItemsCount());
        }
    }
}