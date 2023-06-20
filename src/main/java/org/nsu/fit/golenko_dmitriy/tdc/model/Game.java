package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;

@Log4j2
public class Game implements ModelGameListener {

    private final Configuration settings;
    private final Road road;
    private final ActionListener listener;
    @Getter
    private boolean loop;

    public Game(Configuration settings, ActionListener actionListener) {
        this.road = new Road(settings, this);
        this.settings = settings;
        this.listener = actionListener;
    }

    public void start() {
        // CR: check spawns
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
                road.update();
                listener.update(new GameDTO(settings.roadLength(), road.getEntitiesObjects(), road.getDefeatedEnemy(),
                        road.getMainTowerHealth()));
                lastUpdate = System.currentTimeMillis();
            }
            if (enemySpawnTimePassed > enemySpawnCooldown) {
                log.info("Time " + enemyLastSpawn);
                road.insert(EntityCreator.create(Type.DEFAULT_ENEMY), settings.roadLength() - 1);
                enemyLastSpawn = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void end() {
        // CR: check listener is called
        loop = false;
        road.clear();
        listener.end();
    }

    public void createTower(int cell) throws EntityCreationException {
        // CR: check new enemy on the road
        road.insert(EntityCreator.create(Type.DEFAULT_TOWER), cell);
    }
}