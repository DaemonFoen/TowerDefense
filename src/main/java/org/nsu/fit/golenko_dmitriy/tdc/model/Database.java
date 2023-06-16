package org.nsu.fit.golenko_dmitriy.tdc.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.exception.DataBaseException;

@Log4j2
public abstract class Database {
    protected String DATABASE_FILE;
    protected Map<String, String> dataMap;

    protected void loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            // CR: stream, nio
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    // CR: naming
                    String username = parts[0];
                    String password = parts[1];
                    dataMap.put(username, password);
                }
            }
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    // CR: move to Players db
   public void addUser(String username, String data) {
        log.info("Add user in " + this.getClass().toString());
        dataMap.put(username,data);
        // CR: do it after game close only
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE,true))) {
            writer.write(username + ":" + data);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
