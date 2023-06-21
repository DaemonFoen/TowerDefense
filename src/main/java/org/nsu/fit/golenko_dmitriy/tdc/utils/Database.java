package org.nsu.fit.golenko_dmitriy.tdc.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class Database<K, V> {

    private final Path dbFile;
    protected final Map<K, V> database;

    public Database(Path dbFile, Map<K,V> database) {
        this.dbFile = dbFile;
        this.database = database;
    }

    public void flush() {
        log.info("flush: " + database.toString());
        try {
            Files.write(dbFile, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
            database.forEach(
                    (key, value) -> {
                        try {
                            Files.writeString(dbFile, key + ":" + value + '\n', StandardOpenOption.APPEND);
                        } catch (IOException exception) {
                            log.error("Error writing players database " + exception.getMessage());
                            try {
                                Files.write(dbFile, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
                            } catch (IOException e) {
                                log.fatal("Error clearing players database " + e.getMessage());
                            }
                        }
                    });
        } catch (IOException exception) {
            log.fatal("Error clearing players database " + exception.getMessage());
        }
    }
}