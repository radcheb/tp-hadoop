package com.radcheb.sysdis.etapes.etape9;

import com.radcheb.sysdis.utils.ConfUtils;
import com.radcheb.sysdis.utils.ProcessUtils;
import com.radcheb.sysdis.utils.Task;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.radcheb.sysdis.etapes.etape7.Etape7_40.getValidHosts;

public class Etape7_47 {

    private final static Logger logger = Logger.getLogger(Etape7_47.class);
    private final static String SPLIT_MKDIR_TEMPLATE = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no %s@%s mkdir -p /tmp/jcast/splits";
    private final static String SPLIT_COPY_TEMPLATE = "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no %s %s@%s:/tmp/jcast/splits/";
    private final static String MAPPER_TEMPLATE = "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no %s@%s java -cp /tmp/jcast/data/jar/%s com.radcheb.sysdis.slave.mapper.Mapper %s /tmp/jcast/splits/%s";

    public static void main(String[] args) throws InterruptedException, IOException {
        new Etape7_47().exec(args);
    }

    private void exec(String[] args) throws InterruptedException, IOException {

        if (args.length < 1) {
            logger.error("Not enough argument provided, missing mapper class");
            System.exit(1);
        }

        String mapperClass = args[0];
        ArrayList<String> hosts;
        if (args.length >= 2) {
            hosts = getValidHosts(args[1]);
        } else {
            hosts = getValidHosts(Etape7_47.class.getResourceAsStream("/hosts"));
        }

        ConfUtils.loadConf(Etape7_47.class.getResourceAsStream("/config.properties"));

        List<Task> taskList = IntStream.range(0, ConfUtils.getSplitsNbr()).mapToObj(index ->
                new Task.TaskBuilder(index)
                        .setHostname(hosts.get(index % hosts.size())) // Set Hostname from hostname list
                        .setInputUri(String.format(ConfUtils.getSplitsDirTemplate(), index))
                        .setOutputUri("")
                        .build()).collect(Collectors.toList());

        ConcurrentHashMap<String, Set<String>> keysUms = new ConcurrentHashMap<>();


        boolean frs = taskList.parallelStream().map(task -> {
            String splitPath = String.format(ConfUtils.getSplitsDirTemplate(), task.getIndex());
            String splitName = String.format("S%d.txt", task.getIndex());
            logger.info("copying " + splitPath + " to " + task.getHostname());
            String mkdirCommand = String.format(SPLIT_MKDIR_TEMPLATE, ConfUtils.getUsername(), task.getHostname());

            String scpCommand = String.format(SPLIT_COPY_TEMPLATE, splitPath, ConfUtils.getUsername(), task.getHostname());

            String jarName = "tp-hadoop-1.0-SNAPSHOT-shaded.jar";
            String mapCmd = String.format(MAPPER_TEMPLATE, ConfUtils.getUsername(), task.getHostname(), jarName, mapperClass, splitName);

            boolean rs = ProcessUtils.runCommand(mkdirCommand, 60);
            if (!rs) {
                logger.error("command " + mkdirCommand + "failed.");
                System.exit(1);
            }

            rs = ProcessUtils.runCommand(scpCommand, 60);
            if (!rs) {
                logger.error("command " + scpCommand + "failed.");
                System.exit(1);
            }


            String umx = String.format("UM%d", task.getIndex());
            boolean mapRet = ProcessUtils.consumeCommand(mapCmd, 3000, l -> {
                System.out.println(task.getHostname() + " " + l);
                if (!keysUms.containsKey(l)) {
                    keysUms.put(l, new HashSet<>());
                }
                keysUms.get(l).add(umx);
            });

            if (mapRet) {
                logger.info(String.format("UM%d - %s", task.getIndex(), task.getHostname()));
            }

            return mapRet;
        }).reduce((a, b) -> a && b).get();
        logger.info("Final result: " + frs);

        for (String s : keysUms.keySet()) {
            logger.info(s + " - <" + keysUms.get(s).stream().reduce((a, b) -> a + "," + b) + ">");
        }

        ProcessUtils.shutdown();
    }


}