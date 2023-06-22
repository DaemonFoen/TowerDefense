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
    private static Map<String, EnemyProperty> enemyPropertyMap;
    private static Map<String, AllyProperty> allyPropertyMap;

    public static Entity create(Type type) {
        // CR: can also do a singleton
        if (enemyPropertyMap == null || allyPropertyMap == null) {
            enemyPropertyMap = loadEnemyMap();
            allyPropertyMap = loadAllyMap();
        }
        return switch (type) {
            case MAIN -> new Ally(ID++,
                    -1,
                    // CR: we extract property from the map for each 'get'
                    allyPropertyMap.get("main_tower").health(),
                    allyPropertyMap.get("main_tower").damage(),
                    allyPropertyMap.get("main_tower").actionRadius(),
                    System.currentTimeMillis(),
                    allyPropertyMap.get("main_tower").attackReload());
            case DEFAULT_ENEMY -> new Enemy(ID++,
                    -1,
                    enemyPropertyMap.get("default_enemy").health(),
                    enemyPropertyMap.get("default_enemy").damage(),
                    enemyPropertyMap.get("default_enemy").actionRadius(),
                    System.currentTimeMillis(),
                    enemyPropertyMap.get("default_enemy").attackReload(),
                    enemyPropertyMap.get("default_enemy").stepReload(),
                    System.currentTimeMillis());
            case DEFAULT_TOWER -> new Ally(ID++,
                    -1,
                    allyPropertyMap.get("default_tower").health(),
                    allyPropertyMap.get("default_tower").damage(),
                    allyPropertyMap.get("default_tower").actionRadius(),
                    System.currentTimeMillis(),
                    allyPropertyMap.get("default_tower").attackReload());
        };
    }

    private static Map<String, EnemyProperty> loadEnemyMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<EnemyProperty> enemyPropertyList = mapper.readValue(new File(enemyPropertyPath), new TypeReference<>() {});
            return enemyPropertyList.stream().collect(Collectors.toMap(it -> it.type, it -> it));
        } catch (IOException exception) {
            log.fatal("Enemy configuration load error " + exception.getMessage());
            return new HashMap<>() {{
                put("default_enemy", new EnemyProperty("default_enemy",15, 5, 500, 250, 1));
            }};
        }
    }

    private static Map<String, AllyProperty> loadAllyMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<AllyProperty> allyPropertyList = mapper.readValue(new File(allyPropertyPath), new TypeReference<>() {});
            return allyPropertyList.stream().collect(Collectors.toMap(it -> it.type, it -> it));
        } catch (IOException exception) {
            log.fatal("Ally configuration load error  " + exception.getMessage());
            return new HashMap<>() {{
                put("main_tower", new AllyProperty("main_tower",500, 0, 0, 0));
                put("default_tower", new AllyProperty("main_tower",75, 15, 300, 1));
            }};
        }
    }

    private EntityCreator() {
    }

    private static long ID = 0;

    public enum Type {
        DEFAULT_TOWER,
        MAIN,
        DEFAULT_ENEMY
    }

    private record EnemyProperty(String type, int health, int damage, long attackReload, long stepReload, int actionRadius) {

    }

    private record AllyProperty(String type, int health, int damage, long attackReload, int actionRadius) {

    }
}
