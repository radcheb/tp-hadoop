package com.radcheb.sysdis.etapes.etape9;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import static com.radcheb.sysdis.etapes.etape7.Etape7_40.getValidHosts;
import static com.radcheb.sysdis.etapes.etape7.Etape7_41.runCommand;

public class Etape7_43 {

    private final static Logger logger = Logger.getLogger(Etape7_43.class);
    private final static String SPLIT_MKDIR_TEMPLATE = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no rchebaane@%s mkdir -p /tmp/jcast/splits";
    private final static String SPLIT_COPY_TEMPLATE = "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no %s rchebaane@%s:/tmp/jcast/splits/";

    public static void main(String[] args) throws InterruptedException {

        String hostsFile;
        if (args.length > 0) {
            hostsFile = args[0];
        } else {
            hostsFile = Etape7_43.class.getResource("/hosts").getFile();
        }
        ArrayList<String> hosts = getValidHosts(hostsFile);

        String splitsBase = Etape7_43.class.getResource("/splits").getFile();
        String SPLIT_NAME_TEMPLATE = splitsBase + "/S%d.txt";

        HashMap<String, Integer> splitsMap = new HashMap<>();

        IntStream.range(0, 3).forEach(i -> splitsMap.put(hosts.get(i), i));

        hosts.parallelStream().limit(3).forEach(h -> {
            String splitName = String.format(SPLIT_NAME_TEMPLATE, splitsMap.get(h));
            logger.info("copying " + splitName + " to " + h);
            String mkdirCommand = String.format(SPLIT_MKDIR_TEMPLATE, h);
            String scpCommand = String.format(SPLIT_COPY_TEMPLATE, splitName , h);
            runCommand(mkdirCommand, 60);
            runCommand(scpCommand, 60);
        });
    }
}