package org.nsu.fit.golenko_dmitriy.tdc.model.client.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LobbyDto {
    String id;
    Long createdAt;
    String adminSessionId;

    @Builder.Default
    List<String> members = new ArrayList<>();
}
