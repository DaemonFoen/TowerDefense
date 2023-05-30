package org.nsu.fit.golenko_dmitriy.tdc.exception;

public class AuthException extends GeneralClientException {
    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}