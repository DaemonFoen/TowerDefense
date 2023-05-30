package org.nsu.fit.golenko_dmitriy.tdc.model.client.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompSession;

public interface HandleTransportErrorListener {
    void handleTransportErrorListener(@NotNull StompSession session, @NotNull Throwable e);
}

