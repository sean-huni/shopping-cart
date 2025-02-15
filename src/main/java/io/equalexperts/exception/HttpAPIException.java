package io.equalexperts.exception;

public class HttpAPIException extends RuntimeException {
    public HttpAPIException(String message) {
        super(message);
    }

    public HttpAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
