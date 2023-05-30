package org.nsu.fit.golenko_dmitriy.tdc.model.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

@Slf4j
public class SimpleStompSessionHandler extends StompSessionHandlerAdapter {
    private final AfterConnectedListener connectedListener;
    private final HandleExceptionListener exceptionListener;
    private final HandleTransportErrorListener disconnectedListener;

    public SimpleStompSessionHandler(
            AfterConnectedListener connectedListener,
            HandleExceptionListener exceptionListener,
            HandleTransportErrorListener disconnectedListener
    ) {
        this.connectedListener = connectedListener;
        this.exceptionListener = exceptionListener;
        this.disconnectedListener = disconnectedListener;
    }

    @Override
    public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
        log.debug("afterConnected() : session.getSessionId()=" + session.getSessionId() + ", connectedHeaders=" + connectedHeaders);
        connectedListener.afterConnected(session, connectedHeaders);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, @NotNull StompHeaders headers, @NotNull byte[] payload, Throwable e) {
        log.error("handleException() : session.getSessionId()=" + session.getSessionId() + ", with message : " + e.getMessage());
        exceptionListener.handleException(session, command, headers, payload, e);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable e) {
        log.error("handleException() : session.getSessionId()=" + session.getSessionId() + ", with message : " + e.getMessage());
        disconnectedListener.handleTransportErrorListener(session, e);
    }
}
