package io.equalexperts.component.facade.impl;

import io.equalexperts.component.calculator.impl.CartCalculatorImpl;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.component.facade.CartFacade;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.model.ItemMetadata;
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
    class whenCheckoutAndShowTotalsIsCalledPositiveScenarios {

        @Test
        @DisplayName("Then calculate totals of Cart")
        void shouldAddProductToCartAndCalculateTotals() {
            // Given
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(2.52));
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 6), BigDecimal.valueOf(2.52));
            cartFacade.checkoutAndShowTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(9.98));
            cartFacade.checkoutAndShowTotals(new ProductIn("cheerios", 3), BigDecimal.valueOf(1.33));

            final var productIn = new ProductIn("frosties", 3);
            final var price = BigDecimal.valueOf(3.09);

            // When
            final var consolidatedCart = cartFacade.checkoutAndShowTotals(productIn, price);

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
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 6), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("cheerios", 3), BigDecimal.valueOf(00.00));

            final var productIn = new ProductIn("frosties", 3);
            final var price = BigDecimal.valueOf(00.00);

            // When
            final var consolidatedCart = cartFacade.checkoutAndShowTotals(productIn, price);

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
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(2.52));
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(2.52));
            cartFacade.checkoutAndShowTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(9.98));
            cartFacade.checkoutAndShowTotals(new ProductIn("shreddies", 1), BigDecimal.valueOf(17.23));
            cartFacade.checkoutAndShowTotals(new ProductIn("frosties", 1), BigDecimal.valueOf(4.11));
            final var consolidatedCart = cartFacade.checkoutAndShowTotals(productIn, price);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var itemsInCart = consolidatedCart.shoppingCart();

            assertNotNull(itemsInCart);
            assertNull(consolidatedCart.errors());
            assertEquals(5, itemsInCart.size());    // Categories of items in cart
            assertEquals(2, itemsInCart.get("cornflakes").getQuantity());
            assertEquals(10, itemsInCart.values().stream()
                    .mapToInt(ItemMetadata::getQuantity).sum()); // Total items in cart
        }

        @Test
        @DisplayName("Then verify all items in Cart @100% Discount")
        void shouldAddProductToCartAndVerifyItemsWith100PercentDiscountInCart() {
            // Given
            final var productIn = new ProductIn("cheerios", 3);
            final var price = BigDecimal.valueOf(00.00);
            // When
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("cornflakes", 1), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("weetabix", 3), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("shreddies", 1), BigDecimal.valueOf(00.00));
            cartFacade.checkoutAndShowTotals(new ProductIn("frosties", 1), BigDecimal.valueOf(00.00));
            final var consolidatedCart = cartFacade.checkoutAndShowTotals(productIn, price);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var itemsInCart = consolidatedCart.shoppingCart();

            assertNotNull(itemsInCart);
            assertNull(consolidatedCart.errors());
            assertEquals(5, itemsInCart.size());    // Categories of items in cart
            assertEquals(2, itemsInCart.get("cornflakes").getQuantity());
            assertEquals(10, itemsInCart.values().stream()
                    .mapToInt(ItemMetadata::getQuantity).sum()); // Total items in cart
        }
    }
}