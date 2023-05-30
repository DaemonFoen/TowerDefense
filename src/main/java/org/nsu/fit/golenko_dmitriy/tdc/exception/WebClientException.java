package org.nsu.fit.golenko_dmitriy.tdc.exception;

public class WebClientException extends GeneralClientException {
    public WebClientException(String message) {
        super(message);
    }

    public WebClientException(Throwable cause) {
        super(cause);
    }
}
