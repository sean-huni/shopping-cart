package io.equalexperts.exception;

public class CartException extends RuntimeException {
    private static final long STATUS_CODE = 400L;

    public CartException(String message) {
        super(message);
    }

    public static long getStatusCode() {
        return STATUS_CODE;
    }
}