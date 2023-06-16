package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameEndListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.UpdateListener;

@Log4j2
public class Game implements ModelGameListener {
    private final Road road;
    private final UpdateListener listener;
    private final GameEndListener endListener;
    @Getter
    private boolean loop;
    public Game(UpdateListener updateListener, GameEndListener endListener) {
        this.road = new Road(this);
        this.listener = updateListener;
        this.endListener = endListener;
    }
    public void start() {
        // CR: move to config
        int updateCooldown = 600;
        int enemySpawnCooldown = 5000;
        loop = true;
        long lastUpdate = System.currentTimeMillis();
        long enemyLastSpawn = System.currentTimeMillis();
        while (loop) {
            long updateTimePassed = System.currentTimeMillis() - lastUpdate;
            long enemySpawnTimePassed = System.currentTimeMillis() - enemyLastSpawn;
            if (updateTimePassed > updateCooldown) {
                log.info("Time " + lastUpdate);
                road.update();
                this.listener.update(new GameDTO(road.getMainTower(), road, road.getDefeatedEnemy()));
                lastUpdate = System.currentTimeMillis();
            }
            if (enemySpawnTimePassed > enemySpawnCooldown) {
                log.info("Time " + enemyLastSpawn);
                road.insert(EntityCreator.create(Type.DEFAULT_ENEMY), road.getLength() - 1);
                enemyLastSpawn = System.currentTimeMillis();
            }
        }
    }
    @Override
    public void end() {
        loop = false;
        road.clear();
        endListener.end(road.getDefeatedEnemy());
    }
    // CR: do not add methods for test only
    public void createTower(int cell) throws EntityCreationException {
        road.insert(EntityCreator.create(Type.DEFAULT_TOWER), cell);
    }
    // CR: do not add methods for test only
    public int getRoadLen() {
        return road.getLength();
    }
}
