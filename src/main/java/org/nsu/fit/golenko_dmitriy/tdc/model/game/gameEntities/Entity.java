package org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Entity {
    long id;
    String name;
    int cell;
    int health;
}