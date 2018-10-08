package com.radcheb.sysdis.etapes.etape1;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.radcheb.sysdis.etapes.etape1.Etape1_1.countWords;

public class Etape1_11 {

    private final static Logger logger = Logger.getLogger(Etape1_11.class);

    public static void main(String[] args) {

        String file = Etape1_11.class.getResource("/CC-MAIN-20170322212949-00140-ip-10-233-31-227.ec2.internal.warc.wet").getFile();

        long startTime = System.currentTimeMillis();
        HashMap<String, Integer> wordCounts = countWords(file);
        long totalTime = System.currentTimeMillis() - startTime;
        logger.info(String.format("Counting words in CC-MAIN-20170322212949-00140-ip-10-233-31-227 took %d ms", totalTime));

        startTime = System.currentTimeMillis();
            List<String> sortedKeys = new ArrayList<>(wordCounts.keySet());
            sortedKeys.sort((a, b) -> {
                if (wordCounts.get(b).equals(wordCounts.get(a))) {
                    return a.compareTo(b);
                } else {
                    return wordCounts.get(b) - wordCounts.get(a);
                }
            });
        totalTime = System.currentTimeMillis() - startTime;
        logger.info(String.format("Sorting words counts of CC-MAIN-20170322212949-00140-ip-10-233-31-227 took %d ms", totalTime));

    }

}
