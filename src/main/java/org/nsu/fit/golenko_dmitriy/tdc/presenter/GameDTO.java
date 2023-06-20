package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import java.util.List;
import org.nsu.fit.golenko_dmitriy.tdc.model.Ally;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity;

public record GameDTO(int length, List<EntityObject> entityObjects, int defeatedEnemy, int mainTowerHealth) {
    // CR: do not pass dead objects
    // CR: remove name
    public record EntityObject(long id, int cell, String name, EntityType type, boolean isAlive){}

    public static EntityType entityTypeConvertor(Entity entity){
        if (entity instanceof Ally){
            return EntityType.ALLY;
        }
        return EntityType.ENEMY;
    }
    enum EntityType {
        ENEMY,
        ALLY
    }
}
