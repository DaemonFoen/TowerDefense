package org.nsu.fit.golenko_dmitriy.tdc.model;


import java.util.HashMap;

public class PlayersDB extends Database {
    public PlayersDB(){
        dataMap = new HashMap<>();
        DATABASE_FILE = "src/main/resources/database.txt";
        loadDatabase();
    }

    public boolean userExist(String username){
        return dataMap.containsKey(username);
    }

    public String auth(String username, String password) {
        String storedPassword = dataMap.get(username);
        if (storedPassword != null && storedPassword.equals(password)){
            return username;
        } else {
            return null;
        }
    }
}
