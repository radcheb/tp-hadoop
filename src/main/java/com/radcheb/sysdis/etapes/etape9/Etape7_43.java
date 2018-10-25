package com.radcheb.sysdis.etapes.etape8;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.radcheb.sysdis.etapes.etape7.Etape7_40.getValidHosts;
import static com.radcheb.sysdis.etapes.etape7.Etape7_41.runCommand;

public class Etape7_42 {

    private final static Logger logger = Logger.getLogger(Etape7_42.class);
    private final static String SLAVE_TEMPLATE = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no rchebaane@%s java -jar /tmp/jcast/data/%s/tp-hadoop-1.0-SNAPSHOT.jar";

    public static void main(String[] args) throws InterruptedException {

        String hostsFile;
        if (args.length > 0) {
            hostsFile = args[0];
        } else {
            hostsFile = Etape7_42.class.getResource("/hosts").getFile();
        }
        ArrayList<String> hosts = getValidHosts(hostsFile);

        hosts.parallelStream().forEach( h -> {
                String slaveCommand = String.format(SLAVE_TEMPLATE, h, h);
                runCommand(slaveCommand, 60);
        });
    }
}
