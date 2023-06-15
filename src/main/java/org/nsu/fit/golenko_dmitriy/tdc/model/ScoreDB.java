package org.nsu.fit.golenko_dmitriy.tdc.model;


import java.util.HashMap;

public class ScoreDB extends Database {
    public ScoreDB(){
        dataMap = new HashMap<>();
        DATABASE_FILE = "src/main/resources/score.txt";
        loadDatabase();
    }

    public String score(String username) {
        String score = dataMap.get(username);
        if (score == null){
            addUser(username,"0");
            return "0";
        } else {
            return score;
        }
    }

}

