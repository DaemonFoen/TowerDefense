package org.nsu.fit.golenko_dmitriy.tdc.presenter;

import java.util.List;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity.Team;

public record GameDTO(int length, List<EntityObject> entityObjects, int defeatedEnemy, int mainTowerHealth) {
    public record EntityObject(long id, int cell, String name, EntityType type, boolean isAlive){}

    public static EntityType entityTypeConvertor(Team team){
        if (team == Team.ALLY){
            return EntityType.ALLY;
        }
        return EntityType.ENEMY;
    }
    enum EntityType {
        ENEMY,
        ALLY
    }
}
