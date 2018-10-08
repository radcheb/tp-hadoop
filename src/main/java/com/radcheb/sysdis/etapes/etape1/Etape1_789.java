package com.radcheb.sysdis.etapes.etape1;


import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.radcheb.sysdis.etapes.etape1.Etape1_1.countWords;

public class Etape1_789 {

    private final static Logger logger = Logger.getLogger(Etape1_789.class);

    public static void main(String[] args) {

        String[] files = new String[]{
                Etape1_789.class.getResource("/deontologie_police_nationale.txt").getFile(),
                Etape1_789.class.getResource("/forestier_mayotte.txt").getFile(),
                Etape1_789.class.getResource("/sante_publique.txt").getFile()
        };

        for(String file:files){
            HashMap<String, Integer> wordCounts = countWords(file);

            List<String> sortedKeys = new ArrayList<>(wordCounts.keySet());
            sortedKeys.sort((a, b) -> {
                if (wordCounts.get(b).equals(wordCounts.get(a))) {
                    return a.compareTo(b);
                } else {
                    return wordCounts.get(b) - wordCounts.get(a);
                }
            });

            String filename = (new File(file)).getName();
            System.out.println("Les 5 premier mot de " + filename);
            for (String key : sortedKeys.subList(0, 5)) {
                System.out.println(key + " " + wordCounts.get(key));
            }
        }


    }

}
