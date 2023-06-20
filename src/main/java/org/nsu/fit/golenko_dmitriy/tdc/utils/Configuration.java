package org.nsu.fit.golenko_dmitriy.tdc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;

@Log4j2
public record Configuration(int updateCooldown, int enemySpawnCooldown, int roadLength, int mainTowerHealth, int mainTowerPosition) {
    private static final String path = "src/main/resources/gameConfig.json";
    private static Configuration configuration;

    public static Configuration getInstance() {
        if (configuration != null) {
            return configuration;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(path);
            configuration = mapper.readValue(file, Configuration.class);
        } catch (IOException exception) {
            log.fatal("Configure file not found " + exception.getMessage());
            configuration = new Configuration(600,5000,15,200,0);
        }
        return configuration;
    }
}