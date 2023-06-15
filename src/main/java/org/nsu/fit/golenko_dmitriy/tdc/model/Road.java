package org.nsu.fit.golenko_dmitriy.tdc.model;

import java.util.ArrayList;
import java.util.List;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;

public class Road {
    private final ModelGameListener listener;
    private final int length = 10;
    private int[] allyDamage;
    private int[] enemyDamage;
    private final Entity mainTower;
    private final List<Entity> enemies;
    private final List<Entity> allies;

    public Road(ModelGameListener listener) {
        this.enemies = new ArrayList<>();
        this.allies = new ArrayList<>();
        this.listener = listener;
        this.mainTower = EntityCreator.create(Type.MAIN);
        allyDamage = new int[length];
        enemyDamage = new int[length];
    }

    public void update() {
        updatePosition();
        mainTower.acceptDamage(updateHealth());
        if (!mainTower.isAlive()) {
            listener.end();
        }
    }

    public void insert(Entity entity, int position) {
        if (position < 0 || position >= length) {
            throw new IndexOutOfBoundsException();
        }
        entity.setCell(position);
        switch (entity.getTeam()) {
            case 0 -> allies.add(entity);
            case 1 -> enemies.add(entity);
        }
    }

    public List<Entity> getEntities() {
        List<Entity> result = new ArrayList<>();
        result.addAll(enemies);
        result.addAll(allies);
        return result;
    }

    private void takeDamage(List<Entity> entities, int[] damage) {
        entities.forEach(it -> it.acceptDamage(damage[it.getCell()]));
        List<Entity> list = entities.stream().filter(Entity::isAlive).toList();
        entities.clear();
        entities.addAll(list);
    }

    public synchronized int updateHealth() {
        allyDamage = new int[length];
        enemyDamage = new int[length];
        calculateDamage(enemies, enemyDamage);
        calculateDamage(allies, allyDamage);
        takeDamage(enemies, allyDamage);
        takeDamage(allies, enemyDamage);
        return enemyDamage[0];
    }

    public synchronized void updatePosition() {
        enemies.forEach(Entity::makeStep);
    }

    private void calculateDamage(List<Entity> entities, int[] damage) {
        entities.forEach(it -> sumOnRange(
                it.getCell() - it.getActionRadius(),
                it.getCell() + it.getActionRadius(),
                it.getDamage(), damage
        ));
    }

    private void sumOnRange(int start, int end, int value, int[] array) {
        if (value == 0) {
            return;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > length) {
            end = length;
        }
        for (int i = start; i < end; ++i) {
            array[i] += value;
        }
    }

    public int getLength() {
        return length;
    }

    public Entity getMainTower() {
        return mainTower;
    }
}
