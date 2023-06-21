package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import java.util.List;
import org.nsu.fit.golenko_dmitriy.tdc.model.Ally;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity;

public record GameDTO(int length, List<EntityObject> entityObjects, int defeatedEnemy, int mainTowerHealth) {
    public record EntityObject(long id, int cell, EntityType type){}

    public static EntityType entityTypeConvertor(Entity entity){
        if (entity instanceof Ally){
            return EntityType.ALLY;
        }
        return EntityType.ENEMY;
    }
    public enum EntityType {
        ENEMY,
        ALLY
    }
}
