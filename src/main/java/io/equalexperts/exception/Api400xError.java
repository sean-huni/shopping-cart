package io.equalexperts.exception;


public class Api400xError extends RuntimeException {
    private final int status;

    public Api400xError(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
