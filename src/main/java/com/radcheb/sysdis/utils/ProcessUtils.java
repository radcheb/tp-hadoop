package com.radcheb.sysdis.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProcessUtils {

    private final static Logger logger = Logger.getLogger(ProcessUtils.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static Future processOutput(Process p, Consumer<String> outF) {

        return executorService.submit(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            br.lines().forEach(outF);
        });
    }

    public static Future printStream(InputStream is) {

        return executorService.submit(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            br.lines().forEach(System.out::println);
        });
    }

    public static String captureOutput(InputStream is) {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(sb::append);
        return sb.toString();
    }
    public static boolean consumeCommand(String command, int timeout, Consumer<String> consumer) {
        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = builder.start();
            Future consumerOp = processOutput(p, consumer);
            boolean ret = p.waitFor(timeout, TimeUnit.SECONDS);
            if(ret){
                if(p.exitValue() == 0){
                    logger.info("Command " + command + " finished successfully");
                    if(! consumerOp.isDone()){
                        consumerOp.cancel(false);
                    }
                    return true;
                } else {
                    logger.info("Command " + command + " exited with non 0, see stderr for more details.");
                    consumerOp.cancel(true);
                    return false;
                }
            } else {
                logger.info("Command " + command + " timeout, killing.");
                consumerOp.cancel(true);
                p.destroy();
                return false;
            }
        } catch (IOException e) {
            logger.error("command " + command + " failed: ", e);
            e.getMessage();
            return false;
        } catch (InterruptedException e) {
            logger.error("Command " + command + " interrupted: ", e);
            return false;
        }
    }
    public static boolean runCommand(String command) {
        return runCommand(command, 300);
    }

    public static boolean runCommand(String command, int timeout){

        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
        builder.redirectErrorStream(true);
        try {
            Process p = builder.start();
            boolean ret = p.waitFor(timeout, TimeUnit.SECONDS);
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

    public static void shutdown(){
        executorService.shutdownNow();
    }
}
