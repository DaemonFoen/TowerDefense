package org.nsu.fit.golenko_dmitriy.tdc.model;

public class Ally extends Entity {
    public Ally(long id,
            String type,
            int cell,
            int health,
            int damage,
            int actionRadius,
            long lastAttackUpdated,
            long attackReload
            ){
        super(id, type, cell, health,damage, actionRadius, lastAttackUpdated, attackReload);
    }
}
