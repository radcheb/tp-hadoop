package com.radcheb.sysdis.etapes.etape1;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Etape1_1 {

    private final static Logger logger = Logger.getLogger(Etape1_1.class);

    public static void main(String[] args) {


        if (args.length < 1) {
            logger.error("Usage: Etape1 <filename>");
            System.exit(1);
        }
        HashMap<String, Integer> wordCounts = countWords(args[0]);
        for (String key : wordCounts.keySet()) {
            System.out.println(key + " " + wordCounts.get(key));
        }
    }

    static HashMap<String, Integer> countWords(String filename) {
        HashMap<String, Integer> wordCounts = new HashMap<>();

        Path path = Paths.get(filename);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] words = line.trim().split("\\s+");

                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    if (!word.isEmpty()) {
                        if (wordCounts.containsKey(word)) {
                            int oldCount = wordCounts.get(word);
                            wordCounts.put(word, oldCount + 1);
                        } else {
                            wordCounts.put(word, 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Exception occured {}", e);
        }
        return wordCounts;
    }
}
