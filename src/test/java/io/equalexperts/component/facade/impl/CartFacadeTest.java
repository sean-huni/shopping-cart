package io.equalexperts.component.facade.impl;

import io.equalexperts.component.calculator.CartCalculator;
import io.equalexperts.component.calculator.impl.CartCalculatorImpl;
import io.equalexperts.component.cart.Cart;
import io.equalexperts.component.cart.impl.CartImpl;
import io.equalexperts.component.tax.TaxCalculator;
import io.equalexperts.component.tax.impl.TaxCalculatorImpl;
import io.equalexperts.model.ProductIn;
import io.equalexperts.service.external.priceclient.PriceAPIClient;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Unit-Tests - Given CartFacade State")
@ExtendWith(MockitoExtension.class)
class CartFacadeTest {
    @Spy
    private final TaxCalculator taxCalculator = new TaxCalculatorImpl(BigDecimal.valueOf(12.5)); // @12.5% tax
    @Spy
    private final Cart cart = new CartImpl();
    @Spy
    private CartCalculator cartCalculator = new CartCalculatorImpl(taxCalculator);
    @Mock
    private PriceAPIClient priceAPIClient;
    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(priceAPIClient, cart, cartCalculator);
    }

    @Nested
    @DisplayName("When checkoutAndShowTotals is called - Positive Scenarios")
    class WhenCheckoutAndShowTotalsIsCalledPositiveScenarios {

        @Test
        @DisplayName("Then calculate totals of Cart")
        void shouldAddProductToCartAndCalculateTotals() {
            // Given
            when(priceAPIClient.getPrice("cornflakes")).thenReturn(BigDecimal.valueOf(1.52));
            when(priceAPIClient.getPrice("weetabix")).thenReturn(BigDecimal.valueOf(0.99));
            when(priceAPIClient.getPrice("cheerios")).thenReturn(BigDecimal.valueOf(3.41));
            when(priceAPIClient.getPrice("frosties")).thenReturn(BigDecimal.valueOf(2.39));

            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 6));
            cartService.validateAndAddToCart(new ProductIn("weetabix", 3));
            cartService.validateAndAddToCart(new ProductIn("cheerios", 3));

            final var productIn = new ProductIn("frosties", 3);

            // When
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var totals = consolidatedCart.totals();

            assertNotNull(totals);
            assertFalse(consolidatedCart.errors().hasErrors());
            assertEquals(4.07, totals.tax().doubleValue());
            assertEquals(32.53, totals.subTotal().doubleValue());
            assertEquals(36.60, totals.total().doubleValue());
        }

        @Test
        @DisplayName("Then calculate totals of Cart with all items @100% Discount")
        void shouldAddProductToCartWith100PercentDiscountAndCalculateTotals() {
            // Given
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 6));
            cartService.validateAndAddToCart(new ProductIn("weetabix", 3));
            cartService.validateAndAddToCart(new ProductIn("cheerios", 3));

            final var productIn = new ProductIn("frosties", 3);
            final var price = BigDecimal.valueOf(00.00);


            // When
            when(priceAPIClient.getPrice(any())).thenReturn(price);
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var totals = consolidatedCart.totals();

            assertNotNull(totals);
            assertFalse(consolidatedCart.errors().hasErrors());
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
            final var weetabixPrice = BigDecimal.valueOf(0.29);

            // When
            when(priceAPIClient.getPrice(any())).thenReturn(price);
            when(priceAPIClient.getPrice("weetabix")).thenReturn(weetabixPrice);
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("weetabix", 3));
            cartService.validateAndAddToCart(new ProductIn("shreddies", 1));
            cartService.validateAndAddToCart(new ProductIn("frosties", 1));
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);
            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var itemsInCart = consolidatedCart.items();

            assertNotNull(itemsInCart);
            assertFalse(consolidatedCart.errors().hasErrors());
            assertEquals(5, consolidatedCart.quantities().categoryItemCount());    // Categories of items in cart
            assertEquals(10, consolidatedCart.quantities().totalItemsCount()); // Frequency of Product in Cart

            consolidatedCart.items().stream().filter(i -> i.productName().equals("cornflakes"))
                    .forEach(i -> assertEquals(2, i.quantity())); // Frequency of Product in Cart
            assertEquals(10, consolidatedCart.quantities().totalItemsCount()); // Total items in cart
        }

        @Test
        @DisplayName("Then verify all items in Cart @100% Discount")
        void shouldAddProductToCartAndVerifyItemsWith100PercentDiscountInCart() {
            // Given
            final var productIn = new ProductIn("cheerios", 3);
            final var price = BigDecimal.valueOf(00.00);

            // When
            when(priceAPIClient.getPrice(any())).thenReturn(price);
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("cornflakes", 1));
            cartService.validateAndAddToCart(new ProductIn("weetabix", 3));
            cartService.validateAndAddToCart(new ProductIn("shreddies", 1));
            cartService.validateAndAddToCart(new ProductIn("frosties", 1));
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());

            final var itemsInCart = consolidatedCart.items();

            assertNotNull(itemsInCart);
            assertFalse(consolidatedCart.errors().hasErrors());
            assertEquals(5, itemsInCart.size());    // Categories of items in cart
            assertEquals(10, consolidatedCart.quantities().totalItemsCount()); // Frequency of Product in Cart
            assertEquals(5, consolidatedCart.quantities().categoryItemCount()); // Frequency of Product in Cart
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
            final var price = BigDecimal.valueOf(1.92);

            // When
            when(priceAPIClient.getPrice(any())).thenReturn(price);
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);
            final var nonExistentItem = consolidatedCart.items().stream().filter(i -> i.productName().equals("non-existent")).findAny();

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());
            assertFalse(consolidatedCart.errors().hasErrors());
            assertTrue(nonExistentItem.isEmpty());
        }

        @Test
        @DisplayName("Then calculate totals of Cart with invalid Product and Price")
        void shouldAddProductToCartWithInvalidProductAndPriceAndCalculateTotals() {
            // Given
            final var productIn = new ProductIn("cornflakes", 1);
            final var price = BigDecimal.valueOf(0.00);

            // When
            when(priceAPIClient.getPrice(any())).thenReturn(price);
            final var consolidatedCart = cartService.validateAndAddToCart(productIn);
            final var cornflakesItem = consolidatedCart.items().stream().filter(i -> i.productName().equals("cornflakes")).findAny().get();

            // Then
            assertNotNull(consolidatedCart);
            assertNotNull(consolidatedCart.totals());
            assertFalse(consolidatedCart.errors().hasErrors());
            assertEquals(0.00, cornflakesItem.price().doubleValue());
            assertEquals(1, cornflakesItem.quantity());
        }
    }
}