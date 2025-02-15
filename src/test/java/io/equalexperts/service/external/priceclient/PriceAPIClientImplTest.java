package io.equalexperts.service.external.priceclient;

import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.HttpAPIException;
import io.equalexperts.service.external.priceclient.impl.PriceAPIClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("int")
@ExtendWith(MockitoExtension.class)
@DisplayName("Int-Tests - Given Mocked PriceAPIClient")
class PriceAPIClientImplTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> httpResponse;
    private PriceAPIClientImpl priceAPIClient;

    @BeforeEach
    void setUp() throws Exception {
        priceAPIClient = new PriceAPIClientImpl();

        // Use reflection to inject mocks with wrong metadata - Negative Testing
        injectMock(priceAPIClient, "PRICE_API_URI", "wrong-price-api-uri");
        injectMock(priceAPIClient, "BASE_URL", "http://wrong-base-url");
        injectMock(priceAPIClient, "httpClient", httpClient);
    }

    // This was a bit of a complex test-case. I chose reflection to inject mocks.
    private void injectMock(final Object target, final String fieldName, final Object mockObject) throws Exception {
        Field field = PriceAPIClientImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mockObject);
    }

    @Nested
    @DisplayName("When fetching price - Negative Int Mocked Tests")
    class GetPriceTest {

        @Test
        @DisplayName("Then handle connection timeout - Verify Mock Invocation")
        void handleConnectionTimeout() throws Exception {
            // Given
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Connection timeout"));

            // When & Then
            final HttpAPIException exception = assertThrows(HttpAPIException.class, () -> priceAPIClient.getPrice("cornflakes"));

            assertTrue(exception.getMessage().contains("Error occurred while making API call"));
            assertInstanceOf(InterruptedException.class, exception.getCause());

            // Verify that the mock was invoked
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("Then handle IO exception - Verify Mock Invocation")
        void handleIOException() throws Exception {
            // Given
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Connection refused"));

            // When & Then
            final HttpAPIException exception = assertThrows(HttpAPIException.class, () -> priceAPIClient.getPrice("cornflakes"));
            assertTrue(exception.getMessage().contains("Error occurred while making API call"));
            assertInstanceOf(IOException.class, exception.getCause());

            // Verify that the mock was invoked
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("Then handle 404 response - Verify Mock Invocation")
        void handle404Response() throws Exception {
            // Given
            when(httpResponse.statusCode()).thenReturn(404);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            // When & Then
            final Api400xError exception = assertThrows(Api400xError.class, () -> priceAPIClient.getPrice("nonexistent"));

            assertEquals(404, exception.getStatus());

            // Verify that the mock was invoked and the status code was checked
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpResponse, times(2)).statusCode();
        }

        @Test
        @DisplayName("Then handle unexpected status code - Verify Mock Invocation")
        void handleUnexpectedStatusCode() throws Exception {
            // Given
            when(httpResponse.statusCode()).thenReturn(500);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            // When & Then
            final HttpAPIException exception = assertThrows(HttpAPIException.class, () -> priceAPIClient.getPrice("cornflakes"));
            assertTrue(exception.getMessage().contains("Failed to fetch product details"));

            // Verify Mock invocation and status code check
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpResponse, times(2)).statusCode();
        }
    }
}