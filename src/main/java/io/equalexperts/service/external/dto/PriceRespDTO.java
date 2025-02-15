package io.equalexperts.service.external.dto;

import java.math.BigDecimal;

/**
 * Sample Json Response:
 * <p>
 * {
 * "title": "Corn Flakes",
 * "price": 2.52
 * }
 *
 * @param price
 */
public record PriceRespDTO(String title, BigDecimal price) {
}
