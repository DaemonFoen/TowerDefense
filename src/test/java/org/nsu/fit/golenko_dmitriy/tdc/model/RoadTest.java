package org.nsu.fit.golenko_dmitriy.tdc.model;

import junit.framework.TestCase;
import org.junit.Test;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;

public class RoadTest {
    @Test
    public void mainTowerIsDeadTest() {
        Configuration settings = new Configuration(90, 100, 15, 0);
        Entity enemy = new Enemy(0, -1, 15, 100, 1, 0, 100, 100, 0);
        Road road = new Road(settings.roadLength());
        road.addEnemy(enemy, 1);
        long before = System.currentTimeMillis();
        while (System.currentTimeMillis() < before + 2000) {
            road.update();
        }
        TestCase.assertEquals(listener.i, 1);
    }

    @Test
    public void enemyDeathTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 1, 0, 0);
        Entity ally = new Ally(2, -1, 15, 5, 500, 250, 1);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 5);
        road.insert(ally, 5);
        road.update();
        TestCase.assertEquals(1, road.getDefeatedEnemy());
    }

    @Test
    public void enemyReceivesDamageDoesNotDieTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 1, 0, 0);
        Entity ally = new Ally(2, -1, 15, 5, 500, 250, 1);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 5);
        road.insert(ally, 5);
        road.update();
        TestCase.assertTrue(enemy.isAlive());
    }

    @Test
    public void allyDeathTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 1, 0, 0);
        Entity ally = new Ally(2, -1, 15, 5, 500, 250, 1);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 5);
        road.insert(ally, 5);
        road.update();
        TestCase.assertEquals(1, road.getEntitiesObjects().size());
    }

    @Test
    public void allyReceivesDamageDoesNotDieTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 1, 0, 0);
        Entity ally = new Ally(2, -1, 31, 5, 500, 250, 1);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 5);
        road.insert(ally, 5);
        road.update();
        TestCase.assertTrue(ally.isAlive());
    }

    @Test
    public void enemyStepTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 1, 0, 0);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 5);
        int cellBefore = road.getEntitiesObjects().get(0).cell();
        road.update();
        TestCase.assertEquals(cellBefore - 1, road.getEntitiesObjects().get(0).cell());
    }

    @Test
    public void enemyStepButNowhereToGoTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 1, 0, 0);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 0);
        int cellBefore = road.getEntitiesObjects().get(0).cell();
        road.update();
        TestCase.assertEquals(cellBefore, road.getEntitiesObjects().get(0).cell());
    }

    @Test
    public void enemyAttackCoolDownTest() {
        Entity enemy = new Enemy(1, -1, 300, 30, 500, 250, 200, 0, 0);
        Entity ally = new Ally(2, -1, 40, 5, 500, 250, 200);
        Road road = new Road(new Configuration(10, 10, 15,0), null);
        road.insert(enemy, 5);
        road.insert(ally, 5);
        road.update();
        road.update();
        TestCase.assertEquals(2, road.getEntitiesObjects().size());
    }
}
