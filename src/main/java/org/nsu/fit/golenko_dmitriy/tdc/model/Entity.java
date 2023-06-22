package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public abstract class Entity {

    protected long id;
    protected int cell;
    protected int health;
    protected final int damage;
    protected final int actionRadius;
    protected long lastAttackUpdated;
    protected final long attackReload;

    public int getDamage() {
        long current = System.currentTimeMillis();
        if (lastAttackUpdated + attackReload > current) {
            return 0;
        }
        lastAttackUpdated = current;
        return damage;
    }

    public void acceptDamage(int value) {
        health -= value;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
