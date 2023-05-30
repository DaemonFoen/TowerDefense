package org.nsu.fit.golenko_dmitriy.tdc.model.client.handler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nsu.fit.golenko_dmitriy.tdc.exception.HandlerException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

@Log4j2
public class ConsistentHandler<T> implements StompFrameHandler {
    protected final static int DEFAULT_LIMIT = 8;
    protected final Class<T> typeParameterClass;
    protected final int limit;
    protected ConcurrentLinkedQueue<T> requestQueue;
    protected CountDownLatch latch = null;

    public ConsistentHandler(Class<T> typeParameterClass) {
        this(typeParameterClass, DEFAULT_LIMIT);
    }

    // Если достигается предел, очередь до последнего элемента очищается.
    public ConsistentHandler(Class<T> typeParameterClass, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be greater than 1");
        }
        this.typeParameterClass = typeParameterClass;
        this.requestQueue = new ConcurrentLinkedQueue<>();
        this.limit = limit;
    }

    @Override
    public @NonNull Type getPayloadType(@NotNull StompHeaders stompHeaders) {
        return typeParameterClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleFrame(@NonNull StompHeaders stompHeaders, Object payload) {
        log.debug("handleFrame() : stompHeaders=" + stompHeaders + ", payload=" + payload);
        T received;
        try {
            received = (T) payload;
        } catch (ClassCastException e) {
            log.fatal(
                    "handleFrame() : stompHeaders=" + stompHeaders
                            + ", payload=" + payload
                            + " : caught exception : " + e.getMessage()
            );
            throw new RuntimeException(e);
        }

        requestQueue.offer(received);

        latch.countDown();

        if (requestQueue.size() != limit) {
            return;
        }

        // Синхронизация согласуется с методами получения запросов
        synchronized (this) {
            if (requestQueue.size() <= 1) {
                return;
            }
            T last = readUntilEnd();
            requestQueue.add(last);
        }
    }

    @SuppressWarnings("unused")
    public @Nullable
    synchronized T getResponse() {
        log.debug("getResponse() : requestQueue.size()=" + requestQueue.size());
        return requestQueue.poll();
    }

    @SuppressWarnings("unused")
    public @Nullable
    synchronized T readAll() {
        log.debug("readAll() : requestQueue.size()=" + requestQueue.size());
        int currentSize = requestQueue.size();
        if (currentSize == 0) {
            return null;
        }

        return readUntilEnd();
    }

    // Читает всю очередь, возвращает последний элемент.
    private T readUntilEnd() {
        ArrayList<T> tmp = new ArrayList<>(requestQueue);
        T last = tmp.get(tmp.size() - 1);
        requestQueue.clear();
        return last;
    }

    @SuppressWarnings({"DataFlowIssue", "unused"})
    public synchronized @NotNull T waitAny(long timeout, TimeUnit unit) throws HandlerException {
        latch = new CountDownLatch(1);
        if (requestQueue.size() > 0) {
            return requestQueue.poll();
        }
        try {
            boolean awaited = latch.await(timeout, unit);
            latch = null;
            if (!awaited) {
                throw new HandlerException("time limit has been exceeded");
            }
            return requestQueue.poll();
        } catch (InterruptedException e) {
            throw new HandlerException(e);
        }
    }
}