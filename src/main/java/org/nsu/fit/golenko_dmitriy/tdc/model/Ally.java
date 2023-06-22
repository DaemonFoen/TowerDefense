package org.nsu.fit.golenko_dmitriy.tdc.model;

public class Ally extends Entity {
    public Ally(long id,
            int cell,
            int health,
            int damage,
            int actionRadius,
            long lastAttackUpdated,
            long attackReload
            ){
        super(id, cell, health, damage, actionRadius, lastAttackUpdated, attackReload);
    }
}
