package org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Road {
    String identifier;
    List<Entity> entities;
}
