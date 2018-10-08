package com.radcheb.sysdis.etapes.etape1;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.radcheb.sysdis.etapes.etape1.Etape1_1.countWords;

public class Etape1_3 {

    private final static Logger logger = Logger.getLogger(Etape1_3.class);

    public static void main(String[] args) {

        new Etape1_3();

        if (args.length < 1) {
            logger.error("Usage: Etape1 <filename>");
            System.exit(1);
        }

        HashMap<String, Integer> wordCounts = countWords(args[0]);

        List<String> sortedKeys = new ArrayList<>(wordCounts.keySet());
        sortedKeys.sort((a, b) -> {
            if (wordCounts.get(b).equals(wordCounts.get(a))) {
                return a.compareTo(b);
            } else {
                return wordCounts.get(b) - wordCounts.get(a);
            }
        });

        for (String key : sortedKeys) {
            System.out.println(key + " " + wordCounts.get(key));
        }

    }

}
