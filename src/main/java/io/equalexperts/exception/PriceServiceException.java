package io.equalexperts.exception;

public class PriceServiceException extends CartException {
    private final String title;

    public PriceServiceException(String title, String message) {
        super(message);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
