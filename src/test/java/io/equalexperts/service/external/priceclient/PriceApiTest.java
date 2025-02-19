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
@DisplayName("Unit-Tests - Given Mocked Pricing-API-Gateway")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PriceApiTest {
    @Mock
    private PriceApi priceApi;

    @BeforeEach
    void setUp() {
        when(priceApi.getPrice("cheerios")).thenReturn(BigDecimal.valueOf(199.09));
        when(priceApi.getPrice("cornflakes")).thenReturn(BigDecimal.valueOf(34.99));
        when(priceApi.getPrice("frosties")).thenReturn(BigDecimal.valueOf(122.32));
        when(priceApi.getPrice("shreddies")).thenReturn(BigDecimal.valueOf(102.43));
        when(priceApi.getPrice("weetabix")).thenReturn(BigDecimal.valueOf(199.39));
        when(priceApi.getPrice("chocolates")).thenReturn(BigDecimal.valueOf(-599.39));
    }

    @Nested
    @DisplayName("When getPrice is called - Positive Scenarios")
    class WhenGetPriceIsCalledPositiveScenarios {
        @Test
        @DisplayName("Then the price of cheerios should be 199.09")
        void shouldReturnCorrectPriceForCheerios() {
            // Given
            final var productName = "cheerios";
            // When
            final var price = priceApi.getPrice(productName);
            // Then
            assertEquals(199.09, price.doubleValue());
        }
    }

    @Nested
    @DisplayName("When getPrice is called - Negative Scenarios")
    class WhenGetPriceIsCalledNegativeScenarios {
        @Test
        @DisplayName("Then the price of cheerios should be 199.09")
        void shouldReturnCorrectPriceForCheerios() {
            // Given
            final var productName = "cheerios";
            // When
            final var price = priceApi.getPrice(productName);
            // Then
            assertEquals(199.09, price.doubleValue());
        }
    }
}