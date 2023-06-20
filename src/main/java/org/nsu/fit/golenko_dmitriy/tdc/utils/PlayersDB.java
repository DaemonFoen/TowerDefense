package org.nsu.fit.golenko_dmitriy.tdc.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.exception.DataBaseException;

@Log4j2
public class PlayersDB {

    private static final String DATABASE_FILE = "src/main/resources/database.txt";
    private final Map<String, String> database;

    private static PlayersDB instance;

    private Map<String, String> loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            return reader.lines().map(it -> Arrays.asList(it.split(":"))).collect(
                    Collectors.toMap(it -> (it.get(0)), it -> (it.get(1))));
        } catch (IOException e) {
            // CR: log, load empty
            throw new DataBaseException(e.getMessage());
        }
    }

    public static PlayersDB getInstance() {
        if (instance == null) {
            instance = new PlayersDB();
        }
        return instance;
    }

    private PlayersDB() {
        database = loadDatabase();
        log.info(database.toString());
    }

    public boolean addUser(String username, String password) {
        assert username != null;
        return database.putIfAbsent(username, password) == null;
    }

    public void flush() {
        // CR: nio
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (var i : database.entrySet()) {
                writer.write(i.getKey() + ":" + i.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            // CR: log, clear file
            throw new DataBaseException(e.getMessage());
        }
    }

    public String auth(String username, String password) {
        String storedPassword = database.get(username);
        return storedPassword != null && storedPassword.equals(password) ? username : null;
    }
}
