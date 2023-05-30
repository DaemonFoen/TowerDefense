package org.nsu.fit.golenko_dmitriy.tdc.model.client.handler;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

@Log4j2
public class ConsumableHandler<T> implements StompFrameHandler {
    protected final Class<T> typeParameterClass;
    protected Consumer<T> handler;

    public ConsumableHandler(Class<T> typeParameterClass, Consumer<T> handler) {
        this.typeParameterClass = typeParameterClass;
        this.handler = handler;
    }

    @NotNull
    @Override
    public Type getPayloadType(@NotNull StompHeaders headers) {
        return typeParameterClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleFrame(@NonNull StompHeaders stompHeaders, Object payload) {
        T received;
        try {
            received = (T) payload;
            handler.accept(received);
        } catch (ClassCastException e) {
            log.fatal(
                    "handleFrame() : stompHeaders=" + stompHeaders
                            + ", payload=" + payload
                            + " : caught exception : " + e.getMessage()
            );
            throw new RuntimeException(e);
        }
    }
}
