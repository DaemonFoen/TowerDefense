package org.nsu.fit.golenko_dmitriy.tdc.model;

import junit.framework.TestCase;
import org.junit.Test;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;

public class GameTest {
    @Test
    public void gameSpawnEnemyTest(){
        class TestListener implements ActionListener {
            int enemyCounter = 0;
            @Override
            public void end(int score) {
            }
            @Override
            public void update(GameDTO data) {
                enemyCounter = data.entityObjects().size();
            }
        }
        TestListener listener = new TestListener();
        Game game = new Game(new Configuration(200,300,30,0), listener);
        long start = System.currentTimeMillis();
        Thread thread = new Thread(game::start);
        thread.start();
        while (System.currentTimeMillis() - start < 3200);
        TestCase.assertEquals(listener.enemyCounter,10);
    }

    @Test
    public void gameSpawnCoolDownTest(){
        class TestListener implements ActionListener {
            int enemyCounter = 0;
            @Override
            public void end(int score) {
            }
            @Override
            public void update(GameDTO data) {
                enemyCounter = data.entityObjects().size();
            }
        }
        TestListener listener = new TestListener();
        Game game = new Game(new Configuration(200,300,15,0),listener);
        long start = System.currentTimeMillis();
        Thread thread = new Thread(game::start);
        thread.start();
        while (System.currentTimeMillis() - start < 500);
        TestCase.assertEquals(listener.enemyCounter,1);
    }

    @Test
    public void gameEndTest(){
        class TestListener implements ActionListener {
            int gameEnd = 0;
            @Override
            public void end(int score) {
                gameEnd = 1;
            }
            @Override
            public void update(GameDTO data) {}
        }
        TestListener listener = new TestListener();
        Game game = new Game(new Configuration(200,10,1,0),listener);
        long start = System.currentTimeMillis();
        Thread thread = new Thread(game::start);
        thread.start();
        while (System.currentTimeMillis() - start < 3200);
        TestCase.assertEquals(1,listener.gameEnd);
    }

    @Test
    public void gameCreateTowerTest(){
        class TestListener implements ActionListener {
            int entityCounter = 0;
            @Override
            public void end(int score) {
            }
            @Override
            public void update(GameDTO data) {entityCounter = data.entityObjects().size();
            }
        }
        TestListener listener = new TestListener();
        Game game = new Game(new Configuration(20,300,15,0), listener);
        long start = System.currentTimeMillis();
        Thread thread = new Thread(game::start);
        thread.start();
        game.createTower(2);
        while (System.currentTimeMillis() - start < 200);
        TestCase.assertEquals(1, listener.entityCounter);
    }
}
