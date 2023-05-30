package org.nsu.fit.golenko_dmitriy.tdc.exception;

public class GeneralClientException extends RuntimeException {
    public GeneralClientException(String message) {
        super(message);
    }

    public GeneralClientException(Throwable cause) {
        super(cause);
    }
}