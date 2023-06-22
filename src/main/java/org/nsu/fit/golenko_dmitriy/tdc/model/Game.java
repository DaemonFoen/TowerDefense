package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;

@Log4j2
public class Game {

    private final Configuration settings;
    private final Road road;
    private final ActionListener listener;

    private boolean loop;

    public Game(Configuration settings, ActionListener actionListener) {
        this.road = new Road(settings.roadLength());
        this.settings = settings;
        this.listener = actionListener;
    }

    public void start() {
        int updateCooldown = settings.updateCooldown();
        int enemySpawnCooldown = settings.enemySpawnCooldown();
        loop = true;
        long lastUpdate = System.currentTimeMillis();
        long enemyLastSpawn = System.currentTimeMillis();
        while (loop) {
            long updateTimePassed = System.currentTimeMillis() - lastUpdate;
            long enemySpawnTimePassed = System.currentTimeMillis() - enemyLastSpawn;
            if (updateTimePassed > updateCooldown) {
                log.info("Time " + lastUpdate);
                if (!road.update()) {
                    break;
                }
                listener.update(new GameDTO(settings.roadLength(), road.getEntitiesObjects(), road.getDefeatedEnemy(),
                        road.getMainTowerHealth()));
                lastUpdate = System.currentTimeMillis();
            }
            if (enemySpawnTimePassed > enemySpawnCooldown) {
                log.info("Time " + enemyLastSpawn);
                road.addEnemy(EntityCreator.getInstance().create(Type.DEFAULT_ENEMY), settings.roadLength() - 1);
                enemyLastSpawn = System.currentTimeMillis();
            }
        }
        end();
    }

    public boolean hasEnded() {
        return !loop;
    }

    public void end() {
        loop = false;
        road.clear();
        listener.end(road.getDefeatedEnemy());
    }

    public void createTower(int cell) throws EntityCreationException {
        road.addAlly(EntityCreator.getInstance().create(Type.DEFAULT_TOWER), cell);
    }
}