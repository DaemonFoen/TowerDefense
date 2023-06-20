package org.nsu.fit.golenko_dmitriy.tdc.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.exception.DataBaseException;

//class DB<KeyType, ValueType> {
//    private final String dbFile;
//
//    protected final Map<KeyType, ValueType> data;
//
//    DB(String dbFile) {
//        this.dbFile = dbFile;
//    }
//
//    flush()
//}

@Log4j2
public class ScoreDB {
    private static final String DATABASE_FILE = "src/main/resources/score.txt";
    private final Map<String, Integer> database;
    private static ScoreDB scoreDB;

    private ScoreDB(){
        database = loadDatabase();
        log.info(database.toString());
    }

    public static ScoreDB getInstance(){
        if (scoreDB == null){
            scoreDB = new ScoreDB();
        }
        return scoreDB;
    }

    private Map<String,Integer> loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            return reader.lines().map(it -> Arrays.asList(it.split(":"))).collect(
                    Collectors.toMap(it ->(it.get(0)),it -> (Integer.parseInt(it.get(1)))));
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public void flush(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (var i: database.entrySet()){
                writer.write(i.getKey()+":"+i.getValue().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public record Score(String username, int score){}

    public List<Score> getScores(){
        return database.entrySet().stream().map(it->(new Score(it.getKey(),it.getValue()))).toList();
    }

    public void changeUserScore(String username, Integer score) {
        database.put(username, score);
    }

    public Integer getUserScore(String username) {
        Integer score = database.get(username);
        if (score == null){
            changeUserScore(username,0);
            return 0;
        } else {
            return score;
        }
    }

}