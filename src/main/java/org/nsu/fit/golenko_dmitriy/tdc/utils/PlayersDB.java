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
    private final Map<String,String> database;

    private static PlayersDB playersDB;

    private Map<String,String> loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            return reader.lines().map(it -> Arrays.asList(it.split(":"))).collect(
                    Collectors.toMap(it ->(it.get(0)),it -> (it.get(1))));
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public static PlayersDB getInstance(){
        if (playersDB == null){
            playersDB = new PlayersDB();
        }
        return playersDB;
    }

    private PlayersDB() {
        database = loadDatabase();
        log.info(database.toString());
    }

    public boolean addUser(String username, String data) {
        if (!database.containsKey(username)){
            database.put(username,data);
            return true;
        } else {
            return false;
        }
    }

    public void flush(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (var i: database.entrySet()){
                writer.write(i.getKey()+":"+i.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public String auth(String username, String password) {
        String storedPassword = database.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            return username;
        } else {
            return null;
        }
    }
}
