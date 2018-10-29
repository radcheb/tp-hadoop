package com.radcheb.sysdis.slave.shuffle;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Shuffler {

    private final static Logger logger = Logger.getLogger(Shuffler.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            logger.error("Usage: Shuffler <key> <output SM file> <output UM files>");
            System.exit(1);
        }
        String key = args[0];
        String[] umPaths = Arrays.copyOfRange(args, 2, args.length);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(args[1]))) {
            Arrays.stream(umPaths).parallel().forEach(um -> {
                        logger.info("processing um: " + um);
                        try (BufferedReader reader = Files.newBufferedReader(Paths.get(um))) {
                            reader.lines().filter(l -> l.split(" ")[0].equals(key)).forEach(l -> {
                                try {
                                    writer.write(l + System.lineSeparator());
                                } catch (IOException e) {
                                    logger.error("Failed to write to sm, {}", e);
                                }
                            });
                        } catch (IOException e) {
                            logger.error("Failed open um: " + um + ", {}", e);
                        }
                    }
            );
        }
    }
}
