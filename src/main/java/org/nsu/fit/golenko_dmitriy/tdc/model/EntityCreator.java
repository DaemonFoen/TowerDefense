package org.nsu.fit.golenko_dmitriy.tdc.model;

import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity.Team;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;

public class EntityCreator {
    public static Entity create(Type type) throws EntityCreationException {
        switch (type){
            case MAIN -> {
                return new Entity(ID++, Configuration.getInstance().entityPropertyList().get(0).name(),
                        -1,
                        Configuration.getInstance().entityPropertyList().get(0).health(),
                        Configuration.getInstance().entityPropertyList().get(0).damage(),
                        Configuration.getInstance().entityPropertyList().get(0).attackReload(),
                        Configuration.getInstance().entityPropertyList().get(0).stepReload(),
                        Configuration.getInstance().entityPropertyList().get(0).actionRadius(),
                        Team.ALLY, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_ENEMY -> {
                return new Entity(ID++, Configuration.getInstance().entityPropertyList().get(1).name(),
                        -1,
                        Configuration.getInstance().entityPropertyList().get(1).health(),
                        Configuration.getInstance().entityPropertyList().get(1).damage(),
                        Configuration.getInstance().entityPropertyList().get(1).attackReload(),
                        Configuration.getInstance().entityPropertyList().get(1).stepReload(),
                        Configuration.getInstance().entityPropertyList().get(1).actionRadius(),
                        Team.ENEMY, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_TOWER -> {
                return new Entity(ID++, Configuration.getInstance().entityPropertyList().get(2).name(),
                        -1,
                        Configuration.getInstance().entityPropertyList().get(2).health(),
                        Configuration.getInstance().entityPropertyList().get(2).damage(),
                        Configuration.getInstance().entityPropertyList().get(2).attackReload(),
                        Configuration.getInstance().entityPropertyList().get(2).stepReload(),
                        Configuration.getInstance().entityPropertyList().get(2).actionRadius(),
                        Team.ALLY, System.currentTimeMillis(), System.currentTimeMillis());
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
