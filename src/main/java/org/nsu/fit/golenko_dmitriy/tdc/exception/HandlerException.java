package org.nsu.fit.golenko_dmitriy.tdc.exception;

public class HandlerException extends GeneralClientException {
    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(Throwable cause) {
        super(cause);
    }
}

