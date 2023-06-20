package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class Enemy extends Entity {

    private final long stepReload;
    private long lastStepUpdated;

    public Enemy(long id,
            String type,
            int cell,
            int health,
            int damage,
            int actionRadius,
            long lastAttackUpdated,
            long attackReload,
            long stepReload,
            long lastStepUpdated) {
        super(id, type, cell, health, damage, actionRadius, lastAttackUpdated, attackReload);
        this.stepReload = stepReload;
        this.lastStepUpdated = lastStepUpdated;
    }

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
}



