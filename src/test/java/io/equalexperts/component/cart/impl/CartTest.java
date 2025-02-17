package io.equalexperts.component.cart.impl;

import io.equalexperts.component.cart.Cart;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.ProductIn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@DisplayName("Unit-Tests - Given Cart State")
class CartTest {
    private final Cart cart = new CartImpl();

    @Nested
    @DisplayName("When addProduct is called - Positive Scenarios")
    class WhenAddProductIsCalledPositiveScenarios {

        @Test
        @DisplayName("Then the product should be added to the cart")
        void shouldAddProductToCart() {
            // Given
            final var productIn = new ProductIn("cheerios", 3);
            final var price = BigDecimal.valueOf(199.09);
            // When
            final var items = cart.addProduct(productIn, price);
            // Then
            assertEquals(1, items.size());
            assertEquals(199.09, items.get("cheerios").getPrice().doubleValue());
            assertEquals(3, items.get("cheerios").getQuantity());
        }

        @Test
        @DisplayName("Then multiple products should be in cart")
        void shouldAddMultipleProductsToCart() {
            // Given
            final var cheerios = new ProductIn("cheerios", 1);
            final var cheeriosPrice = BigDecimal.valueOf(19.09);
            final var cornflakes = new ProductIn("cornflakes", 5);
            final var cornflakesPrice = BigDecimal.valueOf(34.99);
            final var chocolates = new ProductIn("chocolates", 15);
            final var chocolatesPrice = BigDecimal.valueOf(37.79);

            // When
            cart.addProduct(cheerios, cheeriosPrice);
            cart.addProduct(chocolates, chocolatesPrice);
            final var items = cart.addProduct(cornflakes, cornflakesPrice);

            // Then
            assertEquals(3, items.size());
            assertEquals(19.09, items.get("cheerios").getPrice().doubleValue());
            assertEquals(1, items.get("cheerios").getQuantity());

            assertEquals(34.99, items.get("cornflakes").getPrice().doubleValue());
            assertEquals(5, items.get("cornflakes").getQuantity());

            assertEquals(37.79, items.get("chocolates").getPrice().doubleValue());
            assertEquals(15, items.get("chocolates").getQuantity());
        }

        @Test
        @DisplayName("Then calculate totals of the products in the cart")
        void shouldCalcTotalsOfMultipleProductsToCart() {
            // Given
            final var cheerios = new ProductIn("cheerios", 1);
            final var cheeriosPrice = BigDecimal.valueOf(19.09);
            final var cornflakes = new ProductIn("cornflakes", 5);
            final var cornflakesPrice = BigDecimal.valueOf(34.99);
            final var chocolates = new ProductIn("chocolates", 15);
            final var chocolate = new ProductIn("chocolates", 1);
            final var chocolatesPrice = BigDecimal.valueOf(37.79);

            // When
            cart.addProduct(chocolate, chocolatesPrice);
            cart.addProduct(cheerios, cheeriosPrice);
            cart.addProduct(chocolates, chocolatesPrice);
            final var items = cart.addProduct(cornflakes, cornflakesPrice);

            // Then
            assertEquals(3, items.size());
            assertEquals(19.09, items.get("cheerios").getPrice().doubleValue());
            assertEquals(1, items.get("cheerios").getQuantity());

            assertEquals(34.99, items.get("cornflakes").getPrice().doubleValue());
            assertEquals(5, items.get("cornflakes").getQuantity());

            assertEquals(37.79, items.get("chocolates").getPrice().doubleValue());
            assertEquals(16, items.get("chocolates").getQuantity());
        }
    }

    @Nested
    @DisplayName("When addProduct is called - Negative Scenarios")
    class WhenAddProductIsCalledNegativeScenarios {

        @Test
        @DisplayName("Then the product should not be added to the cart")
        void shouldNotAddProductToCart() {
            // Given
            final var productIn = new ProductIn("chocolates", 3);

            // When
            final var price = BigDecimal.valueOf(-599.39);

            // Then throw
            assertThrows(CartException.class, () -> cart.addProduct(productIn, price), "Price must be non-negative");
        }

        @Test
        @DisplayName("Then the product should not be added to the cart")
        void shouldAddProductWithZeroQuantityToCart() {
            // Given
            final var productIn = new ProductIn("chocolates", 0);

            // When
            final var price = BigDecimal.valueOf(9.39);
            final var cartState = cart.addProduct(productIn, price);

            // Then
            assertEquals(1, cartState.size());
            assertEquals(0, cartState.get("chocolates").getQuantity());
            assertEquals(9.39, cartState.get("chocolates").getPrice().doubleValue());
        }


        /**
         * The product might be on 100% discount or free 🆓 Yes, we love Freebies too‼️
         * It's added to the cart, but the total should be zero.
         */
        @Test
        @DisplayName("Then the product should not be added to the totals")
        void shouldCalculateProductWithZeroQuantityTotals() {
            // Given
            final var productIn = new ProductIn("chocolates", 0);

            // When
            final var price = BigDecimal.valueOf(9.39);
            final var cartState = cart.addProduct(productIn, price);

            // Then
            assertEquals(1, cartState.size());
        }
    }
}