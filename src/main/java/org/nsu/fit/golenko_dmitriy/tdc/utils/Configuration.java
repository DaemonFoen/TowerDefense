package org.nsu.fit.golenko_dmitriy.tdc.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public record Configuration(List<EntityProperty> entityPropertyList, GameSettings settings) {
    private static final String path = "src/main/resources/config";
    private static Configuration configuration;

    static public Configuration getInstance() {
        if (configuration == null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                List<EntityProperty> entityPropertiesList = reader.lines().takeWhile(it -> (!it.equals(";"))).toList()
                        .stream()
                        .map(it -> Arrays.stream((it.split(":"))).toList())
                        .map(it -> (new EntityProperty(it.get(0), Integer.parseInt(it.get(1)),
                                Integer.parseInt(it.get(2)),
                                Integer.parseInt(it.get(3)), Integer.parseInt(it.get(4)), Integer.parseInt(it.get(5)))))
                        .toList();
                List<Integer> settings = reader.lines().map(Integer::parseInt).toList();
                configuration = new Configuration(entityPropertiesList,
                        new GameSettings(settings.get(0), settings.get(1), settings.get(2)));
            }catch (FileNotFoundException exception){
                log.fatal("Configure file not found" + exception.getMessage());
                System.exit(1);
            }
        }
        return configuration;
    }


    public record EntityProperty(String name, int health, int damage, long attackReload, long stepReload, int actionRadius) {}
    public record GameSettings(int updateCooldown, int enemySpawnCooldown, int roadLength) {}
}