package com.radcheb.sysdis.etapes;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Etape2 {

    final static Logger logger = Logger.getLogger(Etape2.class);

    public static void main(String[] args) {

        if( args.length < 1){
            logger.error("Usage: Etape1 <filename>");
            System.exit(1);
        }

        HashMap<String, Integer> wordCounts = new HashMap<>();

        Path path = Paths.get(args[0]);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] words = line.split("\\s+");

                for(int i = 0; i < words.length; i++){
                    String word = words[i];
                    if(wordCounts.containsKey(word)){
                        int oldCount = wordCounts.get(word);
                        wordCounts.put(word, oldCount+1);
                    } else {
                        wordCounts.put(word, 1);
                    }
                }

                TreeMap<String, Integer> sortedRs = sortWordCount(wordCounts);
                for(String key:sortedRs.keySet()){
                    System.out.println(key + " " + sortedRs.get(key));
                }

            }
        } catch (IOException e){
            logger.error("Exception occured {}", e);
        }

    }

    // Tri le WordCount par ordre de fréquence
    public static TreeMap<String, Integer> sortWordCount(HashMap<String, Integer> wordCount) {

        TreeMap<String,Integer> wordcount_sorted = new TreeMap<>( (a, b) -> {
            if (wordCount.get(a) >= wordCount.get(b)) {
                return -1;
            } else {
                return 1;
            }
        });
        wordcount_sorted.putAll(wordCount);

        return wordcount_sorted;
    }

}
