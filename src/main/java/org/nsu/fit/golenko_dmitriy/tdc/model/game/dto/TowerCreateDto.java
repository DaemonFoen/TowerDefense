package org.nsu.fit.golenko_dmitriy.tdc.model.game.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TowerCreateDto {
    String sessionId;
    String towerName;
    int cellNumber;
}
