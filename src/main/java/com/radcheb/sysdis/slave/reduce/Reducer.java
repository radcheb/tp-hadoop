package com.radcheb.sysdis.slave.reduce;

import com.radcheb.sysdis.example.wordcount.AdditionReducer;
import com.radcheb.sysdis.slave.shuffle.Shuffler;
import com.radcheb.sysdis.utils.Pair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reducer {
    private final static Logger logger = Logger.getLogger(Shuffler.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            logger.error("Usage: Reducer <key> <input SM dir> <output RMs dir>");
            System.exit(1);
        }
        String key = args[0];

        try(BufferedReader reader = Files.newBufferedReader(Paths.get(args[1]))){
            AdditionReducer reducer = new AdditionReducer();

            Pair<String, Long> result = reader.lines().map(l -> {
                String[] tokens = l.split(" ");
                return new Pair<>(tokens[0], Long.valueOf(tokens[1]));
            }).filter(p -> p.getKey().equals(key))
                    .reduce(reducer).orElse(new Pair<>(null, 0L));

            String resultLine = String.format("%s %d", result.getKey(), result.getValue());
            System.out.println(resultLine);
            Files.write(Paths.get(args[2]), resultLine.getBytes());
        }

    }
}
