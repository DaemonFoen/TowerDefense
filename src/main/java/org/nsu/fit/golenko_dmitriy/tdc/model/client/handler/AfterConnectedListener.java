package org.nsu.fit.golenko_dmitriy.tdc.model.client.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

public interface AfterConnectedListener {
    void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders);
}
