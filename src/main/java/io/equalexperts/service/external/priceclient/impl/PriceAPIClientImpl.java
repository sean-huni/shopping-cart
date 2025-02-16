package io.equalexperts.service.external.priceclient.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.equalexperts.exception.Api400xError;
import io.equalexperts.exception.HttpAPIException;
import io.equalexperts.service.external.dto.PriceRespDTO;
import io.equalexperts.service.external.priceclient.PriceAPIClient;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log4j2
public class PriceAPIClientImpl implements PriceAPIClient {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String baseUrl;
    private final String priceApiUri;
    private final HttpClient httpClient;

    public PriceAPIClientImpl() {
        priceApiUri = "backend-take-home-test-data";
        baseUrl = "https://equalexperts.github.io";
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Retrieves the price of a product given its name by interacting with the Price API.
     *
     * @param productName the name of the product whose price is to be fetched
     * @return the price of the product as a {@code BigDecimal}
     */
    @Override
    public BigDecimal getPrice(final String productName) {
        final String endpoint = buildEndpoint(productName);
        final HttpRequest request = buildRequest(endpoint);
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return extractResponse(productName, response);
        } catch (
                IOException e) { // Sonarqube Feedback: Either re-interrupt this method or rethrow the "InterruptedException" that can be caught here.
            log.error("Error occurred while making API call: {}", e.getMessage(), e);
            throw new HttpAPIException("Error occurred while making API call: %s".formatted(e.getMessage()), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            log.error("Thread was interrupted during API call: {}", e.getMessage(), e);
            throw new HttpAPIException("Thread was interrupted: %s".formatted(e.getMessage()), e);
        }
    }


    protected String buildEndpoint(final String productName) {
        return "%s/%s/%s.json".formatted(baseUrl, priceApiUri, productName);
    }

    protected HttpRequest buildRequest(final String endpoint) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(endpoint))
                .build();
    }

    private BigDecimal extractResponse(final String productName, final HttpResponse<String> response) {
        final int statusCode = response.statusCode();

        // Check response status and return the body
        switch (statusCode) {
            case HttpURLConnection.HTTP_OK -> {
                final var gsonResp = gson.toJson(response.body());
                final var priceResp = gson.fromJson(response.body(), PriceRespDTO.class);
                log.debug("Price response: \n{}", gsonResp);
                return priceResp.price();
            }
            case HttpURLConnection.HTTP_NOT_FOUND -> {
                final var errorMsg = "Product %s was not Found. HTTP status: %d".formatted(productName, response.statusCode());
                log.warn(errorMsg);
                throw new Api400xError(HttpURLConnection.HTTP_NOT_FOUND, errorMsg);
            }
            case HttpURLConnection.HTTP_BAD_REQUEST -> {
                final var errorMsg = "Failed Product Request: %s. HTTP status: %d".formatted(productName, response.statusCode());
                log.error(errorMsg);
                throw new Api400xError(HttpURLConnection.HTTP_BAD_REQUEST, errorMsg);
            }
            default -> {
                final var defaultMsg = "Failed to fetch product details. HTTP status: %d".formatted(response.statusCode());
                log.warn("Unexpected status code: {}. {}", response.statusCode(), defaultMsg);
                throw new HttpAPIException(defaultMsg);
            }
        }
    }
}
