package org.nsu.fit.golenko_dmitriy.tdc.model;

import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;

public class EntityCreator {
    public static Entity create(Type type) throws EntityCreationException {
        switch (type){
            case MAIN -> {
                return new Entity(ID++,"main",-1, 200, 0, 0, 0,0, 0, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_ENEMY -> {
                return new Entity(ID++,"default_enemy",-1, 15, 35, 500, 250,1, 1, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_TOWER -> {
                return new Entity(ID++,"default_tower",-1, 75, 15, 500, 300,1, 0, System.currentTimeMillis(), System.currentTimeMillis());
            }
        }
        throw new EntityCreationException("Unidentified Entity type");
    }

    private static long ID = 0;

    public enum Type{
        DEFAULT_TOWER,
        MAIN,
        DEFAULT_ENEMY
    }
}
