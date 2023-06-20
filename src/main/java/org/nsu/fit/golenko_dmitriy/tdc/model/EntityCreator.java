package org.nsu.fit.golenko_dmitriy.tdc.model;

import java.util.List;
import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity.Team;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration.EntityProperty;

public class EntityCreator {

    private static List<EntityProperty> entityPropertyList;
    /*
    CR:
    {
    "type": "MAIN",
    health: 10,
    ...
    },
    {
    "type": TOWER,
    ...
    }
     */

    public static Entity create(Type type) throws EntityCreationException {
        if (entityPropertyList == null) {
            entityPropertyList = Configuration.getInstance().entityPropertyList();
        }
        switch (type) {
            case MAIN -> {
                return new Entity(ID++, entityPropertyList.get(0).name(),
                        -1,
                        entityPropertyList.get(0).health(),
                        entityPropertyList.get(0).damage(),
                        entityPropertyList.get(0).attackReload(),
                        entityPropertyList.get(0).stepReload(),
                        entityPropertyList.get(0).actionRadius(),
                        Team.ALLY, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_ENEMY -> {
                return new Entity(ID++, entityPropertyList.get(1).name(),
                        -1,
                        entityPropertyList.get(1).health(),
                        entityPropertyList.get(1).damage(),
                        entityPropertyList.get(1).attackReload(),
                        entityPropertyList.get(1).stepReload(),
                        entityPropertyList.get(1).actionRadius(),
                        Team.ENEMY, System.currentTimeMillis(), System.currentTimeMillis());
            }
            case DEFAULT_TOWER -> {
                return new Entity(ID++, entityPropertyList.get(2).name(),
                        -1,
                        entityPropertyList.get(2).health(),
                        entityPropertyList.get(2).damage(),
                        entityPropertyList.get(2).attackReload(),
                        entityPropertyList.get(2).stepReload(),
                        entityPropertyList.get(2).actionRadius(),
                        Team.ALLY, System.currentTimeMillis(), System.currentTimeMillis());
            }
        }
        throw new EntityCreationException("Unidentified Entity type");
    }

    private EntityCreator() {
    }

    private static long ID = 0;

    public enum Type {
        DEFAULT_TOWER,
        MAIN,
        DEFAULT_ENEMY
    }
}
