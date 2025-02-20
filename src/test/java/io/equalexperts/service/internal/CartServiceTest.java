package io.equalexperts.service.internal;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.calculator.impl.CartCalculatorImpl;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.exception.Api400xError;
import io.equalexperts.model.ProductIn;
import io.equalexperts.model.ProductRm;
import io.equalexperts.service.external.priceclient.PriceApi;
import io.equalexperts.service.internal.cartengine.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Unit-Tests - Given Mocked CartService")
class CartServiceTest {
    @Spy
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(12.5)); // @12.5% tax
    @Spy
    private final Cart cart = new CartImpl();
    @Spy
    private CartCalculator cartCalculator = new CartCalculatorImpl(taxCalculator);
    @Mock
    private PriceApi priceApi;
    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {

        when(priceApi.getPrice("frosties")).thenReturn(BigDecimal.valueOf(12.34));
        when(priceApi.getPrice("cheerios")).thenReturn(BigDecimal.valueOf(19.09));

        cartService = new CartServiceImpl(priceApi, cart, cartCalculator);
    }

    @Nested
    @DisplayName("Add item to Cart - Positive Scenarios")
    class AddItemToCart {
        @Test
        void validateAndAddToCart() {
            final ProductIn cheerios = new ProductIn("cheerios", 3);
            final var consolidatedCart = cartService.validateAndAddToCart(cheerios);

            final var cheeriosItem = consolidatedCart.items().stream().filter(i -> i.productName().equals("cheerios")).findAny().get();

            assertEquals("cheerios", cheeriosItem.productName());
            assertEquals(19.09, cheeriosItem.price().doubleValue());
        }
    }

    @Nested
    @DisplayName("Add item to Cart - Negative Scenarios")
    class ReturnItemToCartErrors {

        @Test
        void returnCartErrorsWhenAddingToCartMimicAPIServiceUnavailable() {
            when(priceApi.getPrice(any())).thenThrow(new RuntimeException("Price API is down"));

            final ProductIn cheerios = new ProductIn("cheerios", 3);

            final var resp = cartService.validateAndAddToCart(cheerios);
            assertTrue(resp.errors().hasErrors());
            assertEquals("Internal Server Error", resp.errors().errorMessage());
        }

        @Test
        void returnCartErrorsWhenAddingToCart() {
            when(priceApi.getPrice("choco")).thenReturn(BigDecimal.valueOf(-19.09));

            final ProductIn cheerios = new ProductIn("choco", 9);

            final var cartException = cartService.validateAndAddToCart(cheerios);

            assertTrue(cartException.errors().hasErrors());
            assertEquals("'price': Price should not be less than 0.00", cartException.errors().errorMessage().trim());
        }

        @Test
        @DisplayName("Return CartErrors when product is not found (Api400xError)")
        void returnCartErrorsWhenProductNotFound() {
            // Given
            final String productName = "nonexistent-product";
            when(priceApi.getPrice(productName)).thenThrow(new Api400xError(404, "Product not found")); // Simulate Api400xError

            final ProductIn productIn = new ProductIn(productName, 1);

            // When
            final var response = cartService.validateAndAddToCart(productIn);

            // Then
            assertTrue(response.errors().hasErrors());
            assertEquals(404, response.errors().statusCode()); // Verify that the error code matches
            assertEquals("Product %s not found".formatted(productName), response.errors().errorMessage()); // Verify the error message
            verify(priceApi, times(1)).getPrice(productName); // Verify that the getPrice method was called
        }
    }

    @Nested
    @DisplayName("Remove item from Cart - Positive Scenarios")
    class WhenRemoveItemFromCart {
        @Test
        void removeFromCartAndGetTotals() {
            //Given
            final ProductIn cheerios = new ProductIn("cheerios", 3);

            //When
            when(priceApi.getPrice("cornflakes")).thenReturn(BigDecimal.valueOf(19.09));
            cartService.validateAndAddToCart(cheerios);
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 3));

            final var consolidatedCart = cartService.removeFromCartAndGetTotals(new ProductRm("cheerios"));

            assertFalse(consolidatedCart.items().stream().anyMatch(i -> i.productName().equals("cheerios")));
            assertTrue(consolidatedCart.items().stream().anyMatch(i -> i.productName().equals("cornflakes")));
            assertEquals(3, consolidatedCart.quantities().totalItemsCount());
        }
    }
}