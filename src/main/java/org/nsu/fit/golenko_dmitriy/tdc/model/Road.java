package org.nsu.fit.golenko_dmitriy.tdc.model;

import static org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO.entityTypeConvertor;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO.EntityObject;

public class Road {

    private final int length;
    private final Entity mainTower;
    private final List<Entity> enemies;
    private final List<Entity> allies;
    @Getter(AccessLevel.PACKAGE)
    private int defeatedEnemy = 0;

    public void clear() {
        enemies.clear();
        allies.clear();
        defeatedEnemy = 0;
    }

    public Road(int length) {
        this.length = length;
        this.enemies = new ArrayList<>();
        this.allies = new ArrayList<>();
        this.mainTower = EntityCreator.getInstance().create(Type.MAIN);
    }

    public int getMainTowerHealth() {
        return mainTower.getHealth();
    }

    boolean update() {
        updatePosition();
        mainTower.acceptDamage(updateHealth());
        return mainTower.isAlive();
    }

    public void addEnemy(Entity enemy, int pos) {
        enemy.setCell(pos);
        enemies.add(enemy);
    }

    public void addAlly(Entity ally, int pos) {
        ally.setCell(pos);
        allies.add(ally);
    }

    public List<EntityObject> getEntitiesObjects() {
        List<Entity> result = new ArrayList<>();
        result.addAll(enemies);
        result.addAll(allies);
        return result.stream().map(it -> (new EntityObject(it.getId(), it.getCell(), entityTypeConvertor(it)))).toList();
    }

    private void takeDamage(List<Entity> entities, int[] damage) {
        entities.forEach(it -> it.acceptDamage(damage[it.getCell()]));
        List<Entity> alive = entities.stream().filter(Entity::isAlive).toList();
        int allEntity = entities.size();
        entities.clear();
        entities.addAll(alive);
        defeatedEnemy += allEntity - alive.size();
    }

    private int updateHealth() {
        int[] allyDamage = new int[length];
        calculateDamage(allies, allyDamage);
        int[] enemyDamage = new int[length];
        calculateDamage(enemies, enemyDamage);
        takeDamage(enemies, allyDamage);
        takeDamage(allies, enemyDamage);
        return enemyDamage[0];
    }

    private void updatePosition() {
        enemies.stream().map(it -> ((Enemy) it)).forEach(Enemy::makeStep);
    }

    private void calculateDamage(List<Entity> entities, int[] damage) {
        entities.forEach(it -> sumOnRange(
                it.getCell() - it.getActionRadius(),
                it.getCell() + it.getActionRadius(),
                it.getDamage(), damage
        ));
    }

    private void sumOnRange(int start, int end, int damage, int[] array) {
        assert damage >= 0;
        if (damage == 0) {
            return;
        }
        if (start < 0) {
            start = 0;
        }
        if (end >= length) {
            end = length - 1;
        }
        for (int i = start; i <= end; ++i) {
            array[i] += damage;
        }
    }
}
