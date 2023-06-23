package org.nsu.fit.golenko_dmitriy.tdc.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
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
        }
        return instance;
    }

    private EntityCreator() {
    }

    private final Map<String, EnemyProperty> enemyPropertyMap = loadMap(() -> (new HashMap<>() {{
        put("default_enemy", new EnemyProperty("default_enemy", 15, 5, 500, 250, 1));
    }}), enemyPropertyPath, new TypeReference<>() {
    });
    private final Map<String, AllyProperty> allyPropertyMap = loadMap(() -> (new HashMap<>() {{
        put("main_tower", new AllyProperty("main_tower", 500, 0, 0, 0));
        put("default_tower", new AllyProperty("default_tower", 75, 15, 300, 1));
    }}), allyPropertyPath, new TypeReference<>() {
    });

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

    private <T extends Typed> Map<String, T> loadMap(Supplier<Map<String, T>> defaultMap, String propertyPath,
            TypeReference<List<T>> typeRef) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<T> propertyList = mapper.readValue(new File(propertyPath), typeRef);
            return propertyList.stream().collect(Collectors.toMap(it -> it.type(it), it -> it));
        } catch (IOException exception) {
            log.fatal("Configuration load error  " + exception.getMessage());
            return defaultMap.get();
        }
    }


    private static long ID = 0;

    public enum Type {
        DEFAULT_TOWER,
        MAIN,
        DEFAULT_ENEMY
    }


    private interface Typed {

        String type(Typed property);
    }


    private record EnemyProperty(String type, int health, int damage, long attackReload, long stepReload,
                                 int actionRadius) implements Typed {

        @Override
        public String type(Typed property) {
            if (property instanceof EnemyProperty) {
                return ((EnemyProperty) property).type;
            }
            throw new RuntimeException();
        }
    }

    private record AllyProperty(String type, int health, int damage, long attackReload, int actionRadius) implements
            Typed {

        @Override
        public String type(Typed property) {
            if (property instanceof AllyProperty) {
                return ((AllyProperty) property).type;
            }
            throw new RuntimeException();
        }
    }
}
