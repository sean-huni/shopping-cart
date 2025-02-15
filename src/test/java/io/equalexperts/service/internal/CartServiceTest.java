package io.equalexperts.service.internal;

import io.equalexperts.component.impl.CartImpl;
import io.equalexperts.exception.CartException;
import io.equalexperts.model.CartError;
import io.equalexperts.model.CartTotals;
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
class CartServiceTest {
    @Mock
    private ValidatorProvider validator;    // For Product validation
    @Mock
    private PriceAPIClient priceAPIClient;    // For Price validation
    @Mock
    private CartImpl cart;      // For Cart operations
    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        doNothing().when(validator).validateData(new ProductIn("cheerios", 3));
        when(priceAPIClient.getPrice("cheerios")).thenReturn(BigDecimal.valueOf(19.09));
        when(cart.addProduct(new ProductIn("cheerios", 3), BigDecimal.valueOf(19.09))).thenReturn(Map.of("cheerios", new ItemMetadata(BigDecimal.valueOf(19.09), 3)));
        when(cart.getCartTotals()).thenReturn(new CartTotals(BigDecimal.valueOf(19.09), BigDecimal.valueOf(2.38625), BigDecimal.valueOf(21.47625)));
    }

    @Nested
    @DisplayName("Add item to Cart - Positive Scenarios")
    class AddItemToCart {
        @Test
        void validateAndAddToCart() {
            final ProductIn cheerios = new ProductIn("cheerios", 3);
            final var consolidatedCart = cartService.validateAndAddToCart(cheerios);

            assertTrue(consolidatedCart.shoppingCart().containsKey("cheerios"));
            assertEquals(BigDecimal.valueOf(19.09).doubleValue(), consolidatedCart.shoppingCart().get("cheerios").getPrice().doubleValue());
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
            when(cart.addProduct(any(), any())).thenThrow(new CartException("Price must be non-negative"));
            when(validator.buildErrors(any())).thenReturn(new CartError(400L, VALIDATION_ERROR, null, "Price must be non-negative"));

            final ProductIn cheerios = new ProductIn("choco", 9);

            final var cartException = cartService.validateAndAddToCart(cheerios);
            assertEquals("Price must be non-negative", cartException.errors().message());
        }
    }
}