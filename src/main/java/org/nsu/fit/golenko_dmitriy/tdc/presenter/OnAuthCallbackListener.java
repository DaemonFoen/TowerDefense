package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import org.nsu.fit.golenko_dmitriy.tdc.model.client.WebClient;

public interface OnAuthCallbackListener {
    void authorizedSuccessfully(WebClient client, String username);
}
