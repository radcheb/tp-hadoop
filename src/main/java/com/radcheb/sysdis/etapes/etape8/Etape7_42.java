package com.radcheb.sysdis.etapes.etape7;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.radcheb.sysdis.etapes.etape7.Etape7_40.getValidHosts;

public class Etape7_41 {

    private final static Logger logger = Logger.getLogger(Etape7_41.class);
    private final static String MKDIR_TEMPLATE = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no rchebaane@%s mkdir -p /tmp/jcast/data/%s";
    private final static String COPY_TEMPLATE = "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no target/tp-hadoop-1.0-SNAPSHOT.jar rchebaane@%s:/tmp/jcast/data/%s/";

    public static void main(String[] args) throws InterruptedException {

        String hostsFile;
        if (args.length > 0) {
            hostsFile = args[0];
        } else {
            hostsFile = Etape7_41.class.getResource("/hosts").getFile();
        }
        ArrayList<String> hosts = getValidHosts(hostsFile);

        hosts.forEach( h -> {
            Runnable task = () -> {
                String mkdirCommand = String.format(MKDIR_TEMPLATE, h, h);
                String copyCommand = String.format(COPY_TEMPLATE, h, h);

                boolean rs = (runCommand(mkdirCommand, 10) && runCommand(copyCommand, 10));

            };
            new Thread(task).start();
        });
    }

    private static boolean runCommand(String command, int timeout){

        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
        builder.inheritIO();
        try {
            Process p = builder.start();
            boolean ret = p.waitFor(10, TimeUnit.SECONDS);
            if(ret){
                if(p.exitValue() == 0){
                    logger.info("Command " + command + " finished successfully");
                    return true;
                } else {
                    logger.info("Command " + command + " exited with non 0, see stderr for more details.");
                    return false;
                }
            } else {
                logger.info("Command " + command + " timeout, killing.");
                p.destroy();
                return false;
            }
        } catch (IOException e) {
            logger.error("command " + command + " failed: ", e);
            return false;
        } catch (InterruptedException e) {
            logger.error("Command " + command + " interrupted: ", e);
            return false;
        }
    }
}
