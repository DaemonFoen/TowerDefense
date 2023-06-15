package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Entity {
    private long id;
    private String name;
    private int cell;
    private int health;
    private int damage;
    private long attackReload;
    private long stepReload;
    private int actionRadius;
    private int team;
    private long lastAttackUpdated;
    private long lastStepUpdated;

    public int getDamage() {
        long current = System.currentTimeMillis();
        if (lastAttackUpdated + attackReload > current){
            return 0;
        }
        lastAttackUpdated = current;
        return damage;
    }

    public void makeStep() {
        long current = System.currentTimeMillis();
        if (lastStepUpdated + stepReload > current){
            return;
        }
        lastStepUpdated = current;
        if (cell > 0){
            cell = cell - 1;
        } else {
            cell = 0;
        }
    }

    public void acceptDamage(int value) {
        health -= value;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
