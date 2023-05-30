package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import org.jetbrains.annotations.NotNull;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.ExceptionContext;

public interface OnDisconnectedListener {
    void onDisconnect(@NotNull ExceptionContext data, @NotNull Throwable e);
}
