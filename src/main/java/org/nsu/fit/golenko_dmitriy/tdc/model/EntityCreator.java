package org.nsu.fit.golenko_dmitriy.tdc.model;

import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity.Team;

public class EntityCreator {
    public static Entity create(Type type) throws EntityCreationException {
        switch (type){
            case MAIN -> {
                return new Entity(ID++,"main",-1, 200, 0, 0, 0,0, Team.ALLY, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_ENEMY -> {
                return new Entity(ID++,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_TOWER -> {
                return new Entity(ID++,"default_tower",-1, 75, 15, 500, 300,1, Team.ALLY, System.currentTimeMillis(), System.currentTimeMillis());
            }
        }
        throw new EntityCreationException("Unidentified Entity type");
    }
    private EntityCreator(){}
    private static long ID = 0;
    public enum Type{
        DEFAULT_TOWER,
        MAIN,
        DEFAULT_ENEMY
    }
}
