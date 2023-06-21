package org.nsu.fit.golenko_dmitriy.tdc.utils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScoreDB extends Database<String,Integer> {
    private static final Path DATABASE_FILE = Path.of("src/main/resources/score");
    private static ScoreDB instance;

    private ScoreDB(Path DATABASE_FILE, Map<String,Integer> database){
        super(DATABASE_FILE,database);
    }

    public static ScoreDB getInstance(){
        if (instance == null){
            instance = new ScoreDB(DATABASE_FILE, loadDatabase());
        }
        log.info(instance.database.toString());
        return instance;
    }


    public record Score(String username, int score){}

    public List<Score> getScores(){
        return database.entrySet().stream().map(it->(new Score(it.getKey(),it.getValue()))).toList();
    }

    public void changeUserScore(String username, Integer score) {
        database.put(username, score);
    }


    private static Map<String, Integer> loadDatabase() {
        try (Stream<String> lines = Files.lines(DATABASE_FILE)) {
            return lines.map(it -> Arrays.asList(it.split(":"))).collect(
                    Collectors.toMap(it -> (it.get(0)), it -> (Integer.parseInt(it.get(1)))));
        } catch (IOException exception) {
            log.error("Error loading players database " + exception.getMessage());
            return new HashMap<>();
        }
    }

}