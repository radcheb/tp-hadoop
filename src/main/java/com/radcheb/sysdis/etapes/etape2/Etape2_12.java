package com.radcheb.sysdis.etapes.etape2;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Etape2_12 {

    private final static Logger logger = Logger.getLogger(Etape2_12.class);

    public static void main(String[] args) {

        Runtime rt = Runtime.getRuntime();

        try {
            Process proc = rt.exec("hostname");
            String hostname = captureOutput(proc.getInputStream());
            System.out.println("Hostname is " +  hostname);

            proc = rt.exec("hostname -f");
            String fullHostname = captureOutput(proc.getInputStream());
            System.out.println("Full hostname is " +  fullHostname);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class PrintOutput extends Thread {
        InputStream is = null;

        PrintOutput(InputStream is) {
            this.is = is;
        }

        public void run() {
            String s = null;
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                while ((s = br.readLine()) != null) {
                    System.out.println(s);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static String captureOutput(InputStream is) {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(sb::append);
        return sb.toString();
    }
}
