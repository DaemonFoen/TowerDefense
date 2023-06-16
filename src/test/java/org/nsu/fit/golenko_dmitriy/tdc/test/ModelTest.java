package org.nsu.fit.golenko_dmitriy.tdc.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity.Team;
import org.nsu.fit.golenko_dmitriy.tdc.model.Game;
import org.nsu.fit.golenko_dmitriy.tdc.model.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.model.ModelGameListener;
import org.nsu.fit.golenko_dmitriy.tdc.model.Road;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameEndListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.UpdateListener;

public class ModelTest {

    @Test
    public void gameEndTest(){
        class TestListener implements GameEndListener, UpdateListener {
            boolean flag = false;
            @Override
            public void end(int score) {
                flag = true;
            }
            @Override
            public void update(GameDTO data) {
            }
        }
        TestListener listener = new TestListener();
        Game game = new Game(listener,listener);
        game.start();
        while (game.isLoop());
        TestCase.assertTrue(listener.flag);
    }

    @Test
    public void entityDeathTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        entity.acceptDamage(15);
        TestCase.assertFalse(entity.isAlive());
    }

    @Test
    public void entityDamageAcceptedTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        entity.acceptDamage(10);
        TestCase.assertEquals(5, entity.getHealth());
    }

    @Test
    public void entityStepTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        int cellBeforeStep = entity.getCell();
        entity.makeStep();
        TestCase.assertEquals(cellBeforeStep + 1, entity.getCell());
    }

    @Test
    public void entityStepCoolDownTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        entity.makeStep();
        int cellBeforeStep = entity.getCell();
        entity.makeStep();
        TestCase.assertEquals(cellBeforeStep, entity.getCell());
    }

    @Test
    public void entityGetDamageTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        TestCase.assertEquals(5, entity.getDamage());
    }

    @Test
    public void entityGetDamageCoolDownTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        entity.getDamage();
        TestCase.assertEquals(0, entity.getDamage());
    }

    @Test
    public void roadUpdatePositionTest(){
        Road road = new Road(null);
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        road.insert(entity,road.getLength()-1);
        road.update();
        TestCase.assertEquals(road.getEntities().get(0).getCell(),road.getLength()-2);
    }

    @Test
    public void roadUpdateDamageTest(){
        Road road = new Road(null);
        Entity enemy = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        Entity ally = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ALLY, 0, 0);
        road.insert(enemy,road.getLength()-1);
        road.insert(ally,road.getLength()-1);
        road.update();
        TestCase.assertEquals(road.getEntities().get(0).getHealth(), 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void roadInsertOutOfBoundExceptionTest(){
        Road road = new Road(null);
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        road.insert(entity,road.getLength());
    }

    @Test
    public void roadClearTest(){
        Road clearRoad = new Road(null);
        Road road = new Road(null);
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        road.insert(entity, road.getLength()-1);
        int i = 10;
        while (i > 0){
            road.update();
            i--;
        }
        road.clear();
        TestCase.assertEquals(clearRoad.getEntities(),road.getEntities());
    }

    @Test
    public void getEntitiesTest(){
        Road road = new Road(null);
        Entity enemy1 = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        Entity ally1 = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ALLY, 0, 0);
        Entity enemy2 = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        Entity ally2 = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ALLY, 0, 0);
        road.insert(enemy1,road.getLength()-1);
        road.insert(enemy2,road.getLength()-1);
        road.insert(ally1,road.getLength()-1);
        road.insert(ally2,road.getLength()-1);
        TestCase.assertEquals(road.getEntities().size(), 4);
    }

    @Test
    public void roadEndTest(){
        class TestListener implements ModelGameListener{
            int i = 0;
            @Override
            public void end() {
                i = 1;
            }
        }
        TestListener listener = new TestListener();
        Entity enemy = new Entity(0,"default_enemy",-1, 15, 5, 10, 10,1, Team.ENEMY, 0, 0);
        Road road = new Road(listener);
        road.insert(enemy,2);
        while (listener.i == 0){
            road.update();
        }
        TestCase.assertEquals(listener.i, 1);
    }

    @Test
    public void defeatedEnemyCounterTest(){
        Road road = new Road(null);
        Entity ally = new Entity(0,"default_enemy",-1, 100000, 500, 10, 10,1, Team.ALLY, 0, 0);
        Entity enemy = new Entity(0,"default_enemy",-1, 15, 5, 10, 10,1, Team.ENEMY, 0, 0);
        road.insert(ally, road.getLength()-5);
        int i = 200;
        while (i > 0){
            road.insert(enemy,road.getLength()-1);
            i--;
        }
        i = 10000;
        while (i > 0){
            road.update();
            i--;
        }
        TestCase.assertEquals(road.getDefeatedEnemy(),200);
    }

    @Test
    public void Placeholder(){

    }
}
