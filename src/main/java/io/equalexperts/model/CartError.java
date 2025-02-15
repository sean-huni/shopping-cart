package io.equalexperts.model;

import java.util.Map;

public record CartError(
        long statusCode,
        String errorType,
        Map<String, String> violations,
        String message
) {
}
