package com.radcheb.sysdis.etapes.etape2;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Etape2_13 {

    private final static Logger logger = Logger.getLogger(Etape2_13.class);

    public static void main(String[] args) {

        Runtime rt = Runtime.getRuntime();

        try {
            ProcessBuilder b = new ProcessBuilder("/bin/bash", "-x", "-c", "nslookup $HOST | sed \"5q;d\" | awk '{print $2}' && exit 0");
            Process proc = b.start();
            Etape2_12.PrintOutput printOutput = new Etape2_12.PrintOutput(proc.getErrorStream());
            printOutput.start();
            String ip = captureOutput(proc.getInputStream());
            System.out.println("IP is " +  ip);
            proc.destroy();

            Process proc2 = new ProcessBuilder("curl", "ifconfig.me").start();
            ip = captureOutput(proc2.getInputStream());
            System.out.println("External IP is " +  ip);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String captureOutput(InputStream is) {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(sb::append);
        return sb.toString();
    }
}
