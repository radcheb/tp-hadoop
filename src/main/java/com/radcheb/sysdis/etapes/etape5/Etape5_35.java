package com.radcheb.sysdis.etapes.etape5;

import org.apache.log4j.Logger;

import java.io.IOException;

public class Etape5_35 {
    private final static Logger logger = Logger.getLogger(Etape5_35.class);

    public static void main(String[] args) throws InterruptedException {

        try {
            ProcessBuilder b = new ProcessBuilder("ssh", "rchebaane@c133-12", "java", "-jar", "/tmp/jcast/tp-hadoop-1.0-SNAPSHOT.jar");
            b.inheritIO();
            Process proc = b.start();
            proc.waitFor();
            System.out.println("Process finished");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }}
