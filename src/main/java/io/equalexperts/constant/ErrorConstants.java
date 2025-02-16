package io.equalexperts.constant;

import lombok.experimental.UtilityClass;

// Sonarqube Feedback: Move constants defined in this interfaces to another class or enum.
@UtilityClass
public final class ErrorConstants {
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND_ERROR = "NOT_FOUND_ERROR";
    public static final String PRICE_SERVICE_ERROR = "PRICE_SERVICE_ERROR";
}