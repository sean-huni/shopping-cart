package io.equalexperts.exception;

import java.util.Map;

public class CartValidationException extends CartException {
    private final Map<String, String> violations;

    public CartValidationException(String message, Map<String, String> violations) {
        super(message);
        this.violations = violations;
    }

    public Map<String, String> getViolations() {
        return violations;
    }
}
