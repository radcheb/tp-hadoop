package com.radcheb.sysdis.etapes.etape7;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Etape7_40 {

    private final static Logger logger = Logger.getLogger(Etape7_40.class);

    public static void main(String[] args) throws InterruptedException {

        String hostsFile;
        if (args.length > 0) {
            hostsFile = args[0];
        } else {
            hostsFile = Etape7_40.class.getResource("/hosts").getFile();
        }
        getValidHosts(hostsFile).forEach(h -> {
            logger.info(String.format("Host %s is valid.", h));
        });
    }

    public static ArrayList<String> getValidHosts(String hostsFilename) {
        ArrayList<String> hosts = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(hostsFilename))) {
            reader.lines().filter(Etape7_40::verifyHostConnection).forEach(hosts::add);
        } catch (IOException e) {
            logger.error("Can't read hosts file: {}", e);
            System.exit(1);
        }
        return hosts;
    }

    public static boolean verifyHostConnection(String hostname) {
        try (Socket sc = new Socket(hostname, 22)) {
            logger.info("Successfully connected to host " + hostname);
            return true;
        } catch (UnknownHostException e) {
            logger.error("Host " + hostname + " is unknown: {}", e);
            return false;
        } catch (IOException e) {
            logger.error("Exception occured when try to connect to host: {}", e);
            return false;
        }
    }
}
