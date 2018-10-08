package com.radcheb.sysdis.etapes.etape2;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Etape2_15 {

    private final static Logger logger = Logger.getLogger(Etape2_15.class);

    public static void main(String[] args) {

        Runtime rt = Runtime.getRuntime();

        try {
            ProcessBuilder b = new ProcessBuilder("/bin/sh", "-c", "nslookup '137.194.34.76' | grep name | awk '{print $4}'");
            Process proc = b.start();
            Etape2_12.PrintOutput printOutput = new Etape2_12.PrintOutput(proc.getErrorStream());
            String hostname = captureOutput(proc.getInputStream());
            printOutput.start();
            System.out.println("Hostname of 137.194.34.76 is " +  hostname);

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
