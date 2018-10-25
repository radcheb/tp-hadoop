package com.radcheb.sysdis.etapes.etape6;

import org.apache.log4j.Logger;

import java.io.IOException;

public class Etape6_38 {
    private final static Logger logger = Logger.getLogger(Etape6_38.class);

    public static void main(String[] args) throws InterruptedException {

        try {
            ProcessBuilder b = new ProcessBuilder("ssh", "rchebaane@c133-12", "java", "-cp",
                    "/tmp/jcast/tp-hadoop-1.0-SNAPSHOT.jar", "com.radcheb.sysdis.etapes.etape6.WaitCalc");
            b.inheritIO();
            Process proc = b.start();

            proc.waitFor();
            System.out.println("Process finished");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
