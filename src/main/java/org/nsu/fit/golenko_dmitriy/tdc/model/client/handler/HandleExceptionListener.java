package org.nsu.fit.golenko_dmitriy.tdc.model.client.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

public interface HandleExceptionListener {
    void handleException(
            @NotNull StompSession session,
            @NotNull StompCommand command,
            @NotNull StompHeaders headers,
            @NotNull byte[] payload,
            @NotNull Throwable e
    );
}
