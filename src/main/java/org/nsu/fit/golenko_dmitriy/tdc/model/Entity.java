package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Entity {

    private long id;
    private String type;
    private Team team;
    private int cell;
    private int health;

    private final int damage;
    private final long attackReload;
    private final long stepReload;
    private final int actionRadius;
    private long lastAttackUpdated;
    private long lastStepUpdated;

    public int getDamage() {
        long current = System.currentTimeMillis();
        if (lastAttackUpdated + attackReload > current) {
            return 0;
        }
        lastAttackUpdated = current;
        return damage;
    }

    // CR: move to enemy entity
    public void makeStep() {
        long current = System.currentTimeMillis();
        if (lastStepUpdated + stepReload > current) {
            return;
        }
        lastStepUpdated = current;
        if (cell > 0) {
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

    // CR: use entity type
    public enum Team {
        ENEMY, ALLY
    }
}
