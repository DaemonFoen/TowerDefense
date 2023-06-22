package org.nsu.fit.golenko_dmitriy.tdc.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EntityCreator {

    private static final String enemyPropertyPath = "src/main/resources/enemyPropertyConfig.json";
    private static final String allyPropertyPath = "src/main/resources/allyPropertyConfig.json";
    private static EntityCreator instance;

    public static EntityCreator getInstance() {
        if (instance == null) {
            instance = new EntityCreator();
            instance.loadProperty();
        }
        return instance;
    }

    private EntityCreator() {
    }

    private void loadProperty() {
        enemyPropertyMap = loadEnemyMap();
        allyPropertyMap = loadAllyMap();
    }

    private static Map<String, EnemyProperty> enemyPropertyMap;
    private static Map<String, AllyProperty> allyPropertyMap;

    public Entity create(Type type) {
        return switch (type) {
            case MAIN -> {
                AllyProperty property = allyPropertyMap.get("main_tower");
                yield new Ally(ID++,
                        -1,
                        property.health(),
                        property.damage(),
                        property.actionRadius(),
                        System.currentTimeMillis(),
                        property.attackReload());
            }
            case DEFAULT_ENEMY -> {
                EnemyProperty property = enemyPropertyMap.get("default_enemy");
                yield new Enemy(ID++,
                        -1,
                        property.health(),
                        property.damage(),
                        property.actionRadius(),
                        System.currentTimeMillis(),
                        property.attackReload(),
                        property.stepReload(),
                        System.currentTimeMillis());
            }
            case DEFAULT_TOWER -> {
                AllyProperty property = allyPropertyMap.get("default_tower");
                yield new Ally(ID++,
                        -1,
                        property.health(),
                        property.damage(),
                        property.actionRadius(),
                        System.currentTimeMillis(),
                        property.attackReload());
            }
        };
    }

    private static Map<String, EnemyProperty> loadEnemyMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<EnemyProperty> enemyPropertyList = mapper.readValue(new File(enemyPropertyPath),
                    new TypeReference<>() {
                    });
            return enemyPropertyList.stream().collect(Collectors.toMap(it -> it.type, it -> it));
        } catch (IOException exception) {
            log.fatal("Enemy configuration load error " + exception.getMessage());
            return new HashMap<>() {{
                put("default_enemy", new EnemyProperty("default_enemy", 15, 5, 500, 250, 1));
            }};
        }
    }

    private static Map<String, AllyProperty> loadAllyMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<AllyProperty> allyPropertyList = mapper.readValue(new File(allyPropertyPath), new TypeReference<>() {
            });
            return allyPropertyList.stream().collect(Collectors.toMap(it -> it.type, it -> it));
        } catch (IOException exception) {
            log.fatal("Ally configuration load error  " + exception.getMessage());
            return new HashMap<>() {{
                put("main_tower", new AllyProperty("main_tower", 500, 0, 0, 0));
                put("default_tower", new AllyProperty("main_tower", 75, 15, 300, 1));
            }};
        }
    }

    private static long ID = 0;

    public enum Type {
        DEFAULT_TOWER,
        MAIN,
        DEFAULT_ENEMY
    }

    private record EnemyProperty(String type, int health, int damage, long attackReload, long stepReload,
                                 int actionRadius) {

    }

    private record AllyProperty(String type, int health, int damage, long attackReload, int actionRadius) {

    }
}
