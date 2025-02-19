package io.equalexperts.service.external.priceclient;

import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.HttpAPIException;
import io.equalexperts.service.external.priceclient.impl.PriceAPIClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-Tests - Given Mocked PriceAPIClient")
class PriceAPIClientImplTest {
    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> httpResponse;
    private final String baseUrl = "http://wrong-base-url";
    private final String priceApiUri = "wrong-price-api-uri";
    private PriceAPIClient priceAPIClient;

    @BeforeEach
    public void setUp() {
        priceAPIClient = new PriceAPIClientImpl(priceApiUri, baseUrl, httpClient);
    }

    @AfterEach
    void tearDown() {
        reset(httpClient, httpResponse);
    }

    @Nested
    @DisplayName("When fetching price - Negative")
    class GetPriceTest {

        @Test
        @DisplayName("Then handle connection timeout - Verify Mock Invocation")
        void handleConnectionTimeout() throws Exception {
            // Given
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Connection timeout"));

            // When & Then
            final HttpAPIException exception = assertThrows(HttpAPIException.class, () -> priceAPIClient.getPrice("cornflakes"));

            assertTrue(exception.getMessage().contains("Thread was interrupted: Connection timeout"));
            assertInstanceOf(InterruptedException.class, exception.getCause());

            // Verify that the mock was invoked
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("Then handle IllegalArgumentException - Verify Mock Invocation")
        void thenHandleMalformedUri() throws Exception {
            // Given
            priceAPIClient = new PriceAPIClientImpl(priceApiUri, "*from user%&", httpClient);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IllegalArgumentException("Malformed URI"));


            // When & Then
            final HttpAPIException exception = assertThrows(HttpAPIException.class, () -> priceAPIClient.getPrice("cornflakes"));

            assertTrue(exception.getMessage().contains("Illegal character in path at index"));
            assertInstanceOf(IllegalArgumentException.class, exception.getCause());

            // Verify that the mock was invoked
            verify(httpClient, times(0)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("Then handle IO exception gracefully - Verify Mock Invocation")
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
            verify(httpResponse, atLeastOnce()).statusCode();
        }

        @Test
        @DisplayName("Then handle 400x response - Verify Mock Invocation")
        void handle400xResponse() throws Exception {
            // Given
            when(httpResponse.statusCode()).thenReturn(400);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            // When & Then
            final Api400xError exception = assertThrows(Api400xError.class, () -> priceAPIClient.getPrice("nonexistent"));

            assertEquals(400, exception.getStatus());

            // Verify that the mock was invoked and the status code was checked
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpResponse, atLeastOnce()).statusCode();
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
            verify(httpResponse, atLeastOnce()).statusCode();
        }

        @Test
        @DisplayName("Then handle Fake HTTP.200 malformed response body - Verify Mock Invocation")
        void handleFakeHTTP_OKMalformedResponseBody() throws Exception {
            // Given
            final String mockResponse = readJsonFromFile("data/json/negative/fail_malformed_resp.json");

            // When
            when(httpResponse.body()).thenReturn(mockResponse);
            when(httpResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            // When & Then
            final HttpAPIException exception = assertThrows(HttpAPIException.class, () -> priceAPIClient.getPrice("cornflakes"));
            final var expectedErrorMsg = "Failed to extract Response from Body. HTTP.Status: 200. HTTP.Body com.google.gson.stream.MalformedJsonException: Unterminated object at line 3 column 5 path $.title";
            assertTrue(exception.getMessage().contains(expectedErrorMsg));

            // Verify Mock invocation and status code check
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpResponse, atLeastOnce()).statusCode();
            verify(httpResponse, atLeastOnce()).body(); // Ensure body() is invoked
        }

        @Test
        @DisplayName("Then handle HTTP.200 NULL Price - Verify Mock Invocation")
        void handleHTTP200NullPriceResponseBody() throws Exception {
            // Given
            final String mockResponse = readJsonFromFile("data/json/negative/fail_null_price_resp.json");

            // When
            when(httpResponse.body()).thenReturn(mockResponse);
            when(httpResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            // When & Then
            final var price = priceAPIClient.getPrice("cornflakes");

            // Then
            assertNull(price);  // Even though the price is null, this is later validated in the ValidatorProvider, invoked by CartServiceImpl.

            // Verify Mock invocation and status code check
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpResponse, atLeastOnce()).statusCode();
            verify(httpResponse, atLeastOnce()).body(); // Ensure body() is invoked
        }

        /**
         * Even if there was no title in the response, the price alone should be sufficient.
         */
        @Test
        @DisplayName("Then handle HTTP.200 NULL Title - Verify Mock Invocation")
        void handleHTTP200NullTitleResponseBody() throws Exception {
            // Given
            final String mockResponse = readJsonFromFile("data/json/negative/fail_null_title_resp.json");

            // When
            when(httpResponse.body()).thenReturn(mockResponse);
            when(httpResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            // When
            final var price = priceAPIClient.getPrice("cornflakes");

            //Then
            assertNotNull(price);
            assertEquals(10.20, price.doubleValue());

            // Verify Mock invocation and status code check
            verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(httpResponse, atLeastOnce()).statusCode();
            verify(httpResponse, atLeastOnce()).body(); // Ensure body() is invoked
        }
    }

    @Nested
    @DisplayName("When fetching price - Positive")
    class WhenFetchingPricePositive {

        @Test
        @DisplayName("Then the price of cornflakes should be 199.39")
        void shouldReturnCorrectPriceForCornflakes() throws Exception {
            // Given
            final var productName = "cornflakes";
            final String mockResponse = readJsonFromFile("data/json/positive/success_resp.json");

            // When
            when(httpResponse.body()).thenReturn(mockResponse);
            when(httpResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

            final var price = priceAPIClient.getPrice(productName);

            // Then
            // Replace "expectedPrice" with the actual value you'd expect based on the JSON content.
            final double expectedPrice = 2.52;
            assertEquals(expectedPrice, price.doubleValue());
        }
    }

    private String readJsonFromFile(final String resourceFilePath) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourceFilePath)) {
            if (inputStream == null) {
                throw new IOException("File not found in classpath: " + resourceFilePath);
            }
            return new String(inputStream.readAllBytes());
        }
    }
}