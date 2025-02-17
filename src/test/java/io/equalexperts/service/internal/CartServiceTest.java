package io.equalexperts.service.internal;

import io.equalexperts.component.facade.CartFacade;
import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartError;
import io.equalexperts.model.CartTotals;
import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ItemMetadata;
import io.equalexperts.model.ProductIn;
import io.equalexperts.service.external.priceclient.PriceAPIClient;
import io.equalexperts.service.internal.cartengine.CartServiceImpl;
import io.equalexperts.validators.ValidatorProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Map;

import static io.equalexperts.constant.ErrorConstants.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Unit-Tests - Given Mocked CartService")
class CartServiceTest {
    @Mock
    private ValidatorProvider validator;    // For Product validation
    @Mock
    private PriceAPIClient priceAPIClient;    // For Price validation
    @Mock
    private CartFacade cartFacade;      // For Cart operations
    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        final CartTotals cartTotals = new CartTotals(BigDecimal.valueOf(19.09), BigDecimal.valueOf(2.38625), BigDecimal.valueOf(21.47625));
        final ConsolidatedCart consolidatedCart = new ConsolidatedCart(null, Map.of("cheerios", new ItemMetadata(BigDecimal.valueOf(12.34), 3)), cartTotals);

        doNothing().when(validator).validateData(new ProductIn("cheerios", 3));
        when(priceAPIClient.getPrice("cheerios")).thenReturn(BigDecimal.valueOf(19.09));
        when(cartFacade.addToCartAndGetTotals(new ProductIn("cheerios", 3), BigDecimal.valueOf(19.09)))
                .thenReturn(consolidatedCart);
    }

    @Nested
    @DisplayName("Add item to Cart - Positive Scenarios")
    class AddItemToCart {
        @Test
        void validateAndAddToCart() {
            final ProductIn cheerios = new ProductIn("cheerios", 3);
            final var consolidatedCart = cartService.validateAndAddToCart(cheerios);

            assertTrue(consolidatedCart.containsProduct("cheerios"));
            assertEquals(BigDecimal.valueOf(12.34).doubleValue(), consolidatedCart.getPriceForProduct("cheerios").doubleValue());
        }
    }

    @Nested
    @DisplayName("Add item to Cart - Negative Scenarios")
    class ReturnItemToCartErrors {

        @Test
        void returnCartErrorsWhenAddingToCartMimicAPIServiceUnavailable() {
            when(priceAPIClient.getPrice(any())).thenThrow(new RuntimeException("Price API is down"));

            final ProductIn cheerios = new ProductIn("cheerios", 3);

            final var resp = cartService.validateAndAddToCart(cheerios);
            assertEquals("Internal Server Error", resp.errors().message());
        }

        @Test
        void returnCartErrorsWhenAddingToCart() {
            when(cartFacade.addToCartAndGetTotals(any(), any())).thenThrow(new CartException("Price must be non-negative"));
            when(validator.buildErrors(any())).thenReturn(new CartError(400L, VALIDATION_ERROR, null, "Price must be non-negative"));

            final ProductIn cheerios = new ProductIn("choco", 9);

            final var cartException = cartService.validateAndAddToCart(cheerios);
            assertEquals("Price must be non-negative", cartException.errors().message());
        }

        @Test
        @DisplayName("Return CartErrors when product is not found (Api400xError)")
        void returnCartErrorsWhenProductNotFound() {
            // Given
            final String productName = "nonexistent-product";
            when(priceAPIClient.getPrice(productName)).thenThrow(new Api400xError(404, "Product not found")); // Simulate Api400xError

            final ProductIn productIn = new ProductIn(productName, 1);

            // When
            final var response = cartService.validateAndAddToCart(productIn);

            // Then
            assertEquals(404, response.errors().statusCode()); // Verify that the error code matches
            assertEquals("Product %s not found".formatted(productName), response.errors().message()); // Verify the error message
        }
    }
}