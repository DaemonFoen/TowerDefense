package org.nsu.fit.golenko_dmitriy.tdc.model.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionContext {
    String sessionId;
    String destination;
    byte[] payload;
}