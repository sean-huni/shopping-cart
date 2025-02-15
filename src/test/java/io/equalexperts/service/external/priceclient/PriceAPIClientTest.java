package io.equalexperts.service.external.priceclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Given Mocked Pricing-API-Gateway")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PriceAPIClientTest {
    @Mock
    private PriceAPIClient priceAPIClient;

    @BeforeEach
    void setUp() {
        when(priceAPIClient.getPrice("cheerios")).thenReturn(BigDecimal.valueOf(199.09));
        when(priceAPIClient.getPrice("cornflakes")).thenReturn(BigDecimal.valueOf(34.99));
        when(priceAPIClient.getPrice("frosties")).thenReturn(BigDecimal.valueOf(122.32));
        when(priceAPIClient.getPrice("shreddies")).thenReturn(BigDecimal.valueOf(102.43));
        when(priceAPIClient.getPrice("weetabix")).thenReturn(BigDecimal.valueOf(199.39));
        when(priceAPIClient.getPrice("chocolates")).thenReturn(BigDecimal.valueOf(-599.39));
    }

    @Nested
    @DisplayName("When getPrice is called - Positive Scenarios")
    class whenGetPriceIsCalledPositiveScenarios {
        @Test
        @DisplayName("Then the price of cheerios should be 199.39")
        void shouldReturnCorrectPriceForCheerios() {
            // Given
            final var productName = "cheerios";
            // When
            final var price = priceAPIClient.getPrice(productName);
            // Then
            assertEquals(199.09, price.doubleValue());
        }
    }

    @Nested
    @DisplayName("When getPrice is called - Negative Scenarios")
    class whenGetPriceIsCalledNegativeScenarios {
        @Test
        @DisplayName("Then the price of cheerios should be 199.39")
        void shouldReturnCorrectPriceForCheerios() {
            // Given
            final var productName = "cheerios";
            // When
            final var price = priceAPIClient.getPrice(productName);
            // Then
            assertEquals(199.09, price.doubleValue());
        }
    }
}