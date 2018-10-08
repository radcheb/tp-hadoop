package com.radcheb.sysdis.etapes.etape1;


import org.apache.log4j.Logger;

import java.util.*;

import static com.radcheb.sysdis.etapes.etape1.Etape1_1.countWords;

public class Etape1_2 {

    private final static Logger logger = Logger.getLogger(Etape1_2.class);

    public static void main(String[] args) {

        if (args.length < 1) {
            logger.error("Usage: Etape1 <filename>");
            System.exit(1);
        }

        HashMap<String, Integer> wordCounts = countWords(args[0]);

        List<String> sortedKeys = new ArrayList<>(wordCounts.keySet());
        sortedKeys.sort((a, b) -> wordCounts.get(b) - wordCounts.get(a));

        for (String key : sortedKeys) {
            System.out.println(key + " " + wordCounts.get(key));
        }

    }

}
