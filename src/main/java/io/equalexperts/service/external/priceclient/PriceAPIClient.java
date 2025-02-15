package io.equalexperts.service.external.priceclient;

import java.math.BigDecimal;

public interface PriceAPIClient {
    /**
     * Retrieves the price of a product given its name by interacting with the Price API.
     *
     * @param productName the name of the product whose price is to be fetched
     * @return the price of the product as a {@code BigDecimal}
     */
    BigDecimal getPrice(String productName);
}
