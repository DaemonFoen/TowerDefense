package org.nsu.fit.golenko_dmitriy.tdc.model;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.nsu.fit.golenko_dmitriy.tdc.exception.EntityCreationException;
import org.nsu.fit.golenko_dmitriy.tdc.model.EntityCreator.Type;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameEndListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.UpdateListener;

@Log4j2
public class Game implements ModelGameListener {
    private final Road field;
    private final UpdateListener listener;
    private final GameEndListener endListener;
    private int state = 0;
    private ScheduledFuture<?> fieldUpdateTask = null;
    private ScheduledFuture<?> entitySpawnTask = null;

    public Game(UpdateListener updateListener, GameEndListener endListener) {
        this.field = new Road(this);
        this.listener = updateListener;
        this.endListener = endListener;
    }

    public void start() {
        if (state > 0) throw new IllegalStateException("game loop is already started");
        Runnable updateField = () -> {
            log.info("[Runnable] updateField()");
            field.update();
            this.listener.update(new FiledData(field.getMainTower(), field));
        };
        ScheduledExecutorService updateService = Executors.newScheduledThreadPool(2);
        this.fieldUpdateTask = updateService.scheduleWithFixedDelay(updateField, 1000, 300, TimeUnit.MILLISECONDS);


        Runnable spawnEntity = () -> field.insert(EntityCreator.create(Type.DEFAULT_ENEMY), field.getLength() - 1);
        ScheduledExecutorService spawnService = Executors.newScheduledThreadPool(2);
        entitySpawnTask = spawnService.scheduleWithFixedDelay(spawnEntity, 3, 5, TimeUnit.SECONDS);
        state = 1;
    }

    @Override
    public void end() {
        close();
        endListener.end();
    }

    public void close() {
        if (state <= 0) throw new IllegalStateException("could not close ended or not started game");
        fieldUpdateTask.cancel(true);
        entitySpawnTask.cancel(true);
        state = -1;
    }

    public void createTower(int cell) throws EntityCreationException {
        field.insert(EntityCreator.create(Type.DEFAULT_TOWER), cell);
    }
}
