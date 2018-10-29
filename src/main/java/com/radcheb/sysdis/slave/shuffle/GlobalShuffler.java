package com.radcheb.sysdis.slave.shuffle;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalShuffler {

    private final static Logger logger = Logger.getLogger(GlobalShuffler.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            logger.error("Usage: Shuffler <output UM dir> <output SM dir>");
            System.exit(1);
        }

        ConcurrentHashMap<String, BufferedWriter> keysWriters = new ConcurrentHashMap<>();
        AtomicLong idsGen = new AtomicLong(0);
        File UMsDir = new File(args[0]);
        String SMsDir = args[1];

        File[] UMs = UMsDir.listFiles((dir, name) -> name.startsWith("UM"));

        Arrays.stream(Objects.requireNonNull(UMs)).parallel().forEach(um -> {
                    logger.info("processing um: " + um.getName());
                    String SMSplitPathTemp = String.format("%s/SM", SMsDir);
                    try (BufferedReader reader = Files.newBufferedReader(um.toPath())) {
                        reader.lines().forEach(l -> {
                            String key = l.split(" ")[0];

                            keysWriters.computeIfAbsent(key, s -> {
                                try {
                                    String smPath = SMSplitPathTemp + idsGen.incrementAndGet();
                                    return Files.newBufferedWriter(Paths.get(
                                            smPath
                                    ));
                                } catch (IOException e) {
                                    logger.error("Failed to open SM file for writing");
                                    return null;
                                }
                            });

                            try {
                                keysWriters.get(key).write(l + System.lineSeparator());
                            } catch (IOException e) {
                                logger.error("Failed to write line: " + l + ", {}", e);
                            }
                        });
                    } catch (IOException e) {
                        logger.error("Failed open um: " + um + ", {}", e);
                    }
                }
        );

        keysWriters.values().forEach(w -> {
            try {
                w.close();
            } catch (IOException e) {
                logger.error("Failed to close writer {}", e);
            }
        });
    }
}
