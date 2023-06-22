package org.nsu.fit.golenko_dmitriy.tdc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;

@Log4j2
public record Configuration(int updateCooldown, int enemySpawnCooldown, int roadLength, int mainTowerPosition) {
    private static final String CONFIG_JSON = "src/main/resources/gameConfig.json";
    private static Configuration configuration;

    public static Configuration getInstance() {
        if (configuration != null) {
            return configuration;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(CONFIG_JSON);
            configuration = mapper.readValue(file, Configuration.class);
        } catch (IOException exception) {
            log.fatal("Configure file not found " + exception.getMessage());
            configuration = new Configuration(500,2000,15, 0);
        }
        return configuration;
    }
}