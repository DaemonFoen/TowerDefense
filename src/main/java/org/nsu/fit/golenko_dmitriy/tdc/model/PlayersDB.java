package org.nsu.fit.golenko_dmitriy.tdc.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.nsu.fit.golenko_dmitriy.tdc.exception.DataBaseException;

public class PlayersDB {
    private static final String DATABASE_FILE = "src/main/resources/database.txt";
    private final Map<String, String> data;

    public PlayersDB() {
        data = new HashMap<>();
        loadDatabase();
    }

    public void addUser(String username, String password) {
        addPlayer(username, password);
    }

    public boolean auth(String username, String password) {
        String storedPassword = data.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    private void loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    data.put(username, password);
                }
            }
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    private void addPlayer(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            writer.write(username + ":" + password);
            writer.newLine();
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public static void auth(String[] args) {
        PlayersDB database = new PlayersDB();
        database.addUser("john", "password123");
        database.addUser("jane", "qwerty");
        boolean authenticated = database.auth("john", "password123");
        System.out.println("Authentication result: " + authenticated);
    }





}
