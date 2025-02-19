package io.equalexperts.component.cart;

import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.exception.InvalidProductRemovalException;
import io.equalexperts.model.ProductIn;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Unit-Tests - Given CartRemove")
@ExtendWith(MockitoExtension.class)
class CartRemoveTest {
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = spy(new CartImpl());
    }

    @AfterEach
    void tearDown() {
        reset(cart);
    }

    @Nested
    @DisplayName("Remove Product from Cart - Positive Scenarios")
    class WhenRemoveProductFromCart {

        @Test
        @DisplayName("When removing a product from the cart")
        void removeProductFromCart() {
            // Given
            final var productIn = new ProductIn("cornflakes", 3);
            final var price = BigDecimal.valueOf(2.99);
            cart.addProduct(productIn, price);

            // When
            final var cartSummaryView = cart.removeProduct("cornflakes");

            // Then
            assertNotNull(cartSummaryView);
            assertFalse(cartSummaryView.containsKey("cornflakes"));
            assertTrue(cartSummaryView.isEmpty());
            verify(cart, times(1)).removeProduct("cornflakes");
        }
    }

    @Nested
    @DisplayName("Remove Product from Cart - Negative Scenarios")
    class WhenRemoveProductFromCartWithInvalidProduct {

        @Test
        @DisplayName("When removing a product (with Typo) from the cart")
        void removeProductFromCart() {
            // Given
            final var testProduct = "cornflakes";
            final var productIn = new ProductIn(testProduct, 3);
            final var price = BigDecimal.valueOf(2.99);
            cart.addProduct(productIn, price);

            // When
            final var cartSummaryView = cart.removeProduct("Corn Flakes");

            // Then
            assertNotNull(cartSummaryView);
            assertTrue(cartSummaryView.containsKey("cornflakes"));
            assertFalse(cartSummaryView.isEmpty());
            verify(cart, atMost(0)).removeProduct("cornflakes");
        }

        @Test
        @DisplayName("When removing a product (with Blank) from the cart")
        void whenRemovingProductWithBlankName() {
            // Given
            final var productIn = new ProductIn("cornflakes", 3);
            final var price = BigDecimal.valueOf(2.99);
            cart.addProduct(productIn, price);

            // When
            final var cartException = assertThrows(InvalidProductRemovalException.class, () -> cart.removeProduct(""));

            // Then
            assertNotNull(cartException);
            assertEquals("Product name must not be null or blank", cartException.getMessage());
            verify(cart, atMost(0)).removeProduct("cornflakes");
            verify(cart, atMost(1)).removeProduct("");
        }
    }
}