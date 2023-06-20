package org.nsu.fit.golenko_dmitriy.tdc.model;

import junit.framework.TestCase;
import org.junit.Test;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity.Team;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration.GameSettings;

/*

1. main tower is dead
2. enemy receives damage, does not die
3. enemy receives damage, dies (also check counter)
4. ally receives damage, does not die
5. ally receives damage, dies
6. new enemy spawns after cool down
7. enemy makes one step
8. enemy makes one step, nowhere to go
9. enemy cannot attack before cooldown passes
 */
public class RoadTest {

    @Test
    public void gameSpawnEnemyTest(){
        class TestListener implements ActionListener {
            int enemyCounter = 0;
            @Override
            public void end() {
            }
            @Override
            public void update(GameDTO data) {
                enemyCounter = data.entityObjects().size();
            }
        }
        TestListener listener = new TestListener();
        Game game = new Game(new GameSettings(200,300,3000),listener);
        long start = System.currentTimeMillis();
        Thread thread = new Thread(game::start);
        thread.start();
        while (System.currentTimeMillis() - start < 3200);
        TestCase.assertEquals(listener.enemyCounter,10);
    }

    @Test
    public void entityDeathTest(){
        Entity enemy = new Entity(1,"default_enemy",-1, 300, 30, 500, 250,1, Team.ENEMY, 0, 0);
        Entity ally = new Entity(2,"default_tower",-1, 15, 5, 500, 250,1, Team.ALLY, 0, 0);
        Road road = new Road(new GameSettings(10,10,15), null);
        road.insert(enemy,5);
        road.insert(ally,5);
        road.update();
        TestCase.assertEquals(1, road.getEntitiesObjects().size());
    }

    @Test
    public void entityDamageAcceptedTest(){
        Entity enemy = new Entity(1,"default_enemy",-1, 300, 30, 500, 250,1, Team.ENEMY, 0, 0);
        Entity ally = new Entity(2,"default_tower",-1, 40, 5, 500, 250,1, Team.ALLY, 0, 0);
        Road road = new Road(new GameSettings(10,10,15), null);
        road.insert(enemy,5);
        road.insert(ally,5);
        road.update();
        TestCase.assertTrue(ally.isAlive());
    }

    @Test
    public void entityStepTest(){
        Entity enemy = new Entity(1,"default_enemy",-1, 300, 30, 500, 250,1, Team.ENEMY, 0, 0);
        Road road = new Road(new GameSettings(10,10,15), null);
        road.insert(enemy,5);
        int cellBefore = road.getEntitiesObjects().get(0).cell();
        road.update();
        TestCase.assertEquals(cellBefore - 1, road.getEntitiesObjects().get(0).cell());
    }

    @Test
    public void entityGetDamageCoolDownTest(){
        Entity entity = new Entity(0,"default_enemy",-1, 15, 5, 500, 250,1, Team.ENEMY, 0, 0);
        entity.getDamage();
        TestCase.assertEquals(0, entity.getDamage());
    }

    @Test
    public void roadEndCallbackTest(){
        // CR: pass main tower hp in config
        GameSettings settings = new GameSettings(10,10,3);
        class TestListener implements ModelGameListener {
            int i = 0;
            @Override
            public void end() {
                i = 1;
            }
        }
        TestListener listener = new TestListener();
        Entity enemy = new Entity(0,"default_enemy",-1, 15, 5, 10, 10,1, Team.ENEMY, 0, 0);
        Road road = new Road(settings,listener);
        road.insert(enemy,2);
        while (listener.i == 0){
            road.update();
        }
        TestCase.assertEquals(listener.i, 1);
    }

    @Test
    public void defeatedEnemyCounterTest(){
        GameSettings settings = new GameSettings(10,10,15);
        Road road = new Road(settings,null);
        Entity ally = new Entity(0,"default_enemy",-1, 100000, 500, 10, 10,1, Team.ALLY, 0, 0);
        Entity enemy = new Entity(0,"default_enemy",-1, 15, 5, 10, 10,1, Team.ENEMY, 0, 0);
        road.insert(ally, settings.roadLength()-5);
        int i = 200;
        while (i > 0){
            road.insert(enemy,settings.roadLength()-1);
            i--;
        }
        i = 10000;
        while (i > 0){
            road.update();
            i--;
        }
        TestCase.assertEquals(road.getDefeatedEnemy(),200);
    }

}
