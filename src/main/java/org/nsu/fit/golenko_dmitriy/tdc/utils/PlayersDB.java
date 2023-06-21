package org.nsu.fit.golenko_dmitriy.tdc.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlayersDB extends Database<String, String> {

    private static final Path DATABASE_FILE = Path.of("src/main/resources/database");

    private static PlayersDB instance;


    public static PlayersDB getInstance() {
        if (instance == null) {
            instance = new PlayersDB(DATABASE_FILE, loadDatabase());
        }
        log.info(instance.database.toString());
        return instance;
    }

    private PlayersDB(Path DATABASE_FILE, Map<String,String> database) {
        super(DATABASE_FILE, database);
    }

    public boolean addUser(String username, String password) {
        assert username != null;
        return database.putIfAbsent(username, password) == null;
    }


    public String auth(String username, String password) {
        String storedPassword = database.get(username);
        return storedPassword != null && storedPassword.equals(password) ? username : null;
    }

    private static Map<String, String> loadDatabase() {
        try (Stream<String> lines = Files.lines(DATABASE_FILE)) {
            return lines.map(it -> Arrays.asList(it.split(":"))).collect(
                    Collectors.toMap(it -> (it.get(0)), it -> ((it.get(1)))));
        } catch (IOException exception) {
            log.error("Error loading players database " + exception.getMessage());
            return new HashMap<>();
        }
    }

}
