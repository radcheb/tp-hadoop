package com.radcheb.sysdis;

import com.radcheb.sysdis.metrics.HostStat;
import com.radcheb.sysdis.utils.*;
import com.radcheb.sysdis.utils.reduce.ReduceTask;
import com.radcheb.sysdis.utils.shuffle.CopyTask;
import com.radcheb.sysdis.utils.shuffle.ShuffleTask;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.radcheb.sysdis.etapes.etape7.Etape7_40.getValidHosts;

public class Master {

    private final static Logger logger = Logger.getLogger(Master.class);

    private final static String UNCHECKED_SSH = "ssh -q -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ";
    private final static String UNCHECKED_SCP = "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ";
    private final static String SPLIT_MKDIR_TEMPLATE = UNCHECKED_SSH + "%s@%s mkdir -p /tmp/jcast/splits";
    private final static String SPLIT_COPY_TEMPLATE = UNCHECKED_SCP + "%s %s@%s:/tmp/jcast/splits/";
    private final static String MAPPER_TEMPLATE = UNCHECKED_SSH + "%s@%s java -Dlog4j.configurationFile=file:/slave-log4j.properties -cp /tmp/jcast/data/jar/%s com.radcheb.sysdis.slave.Slave 0 %s /tmp/jcast/splits/%s";
    private final static String UM_COPY_TEMPLATE = UNCHECKED_SSH + "%s@%s scp /tmp/jcast/unsorted_map/%s %s@%s:/tmp/jcast/unsorted_map/";
    private final static String SHUFFLER_TEMPLATE = UNCHECKED_SSH + "%s@%s java -Dlog4j.configurationFile=file:/slave-log4j.properties -cp /tmp/jcast/data/jar/%s com.radcheb.sysdis.slave.Slave 1 %s %s %s";
    private final static String REDUCER_TEMPLATE = UNCHECKED_SSH + "%s@%s java -Dlog4j.configurationFile=file:/slave-log4j.properties -cp /tmp/jcast/data/jar/%s com.radcheb.sysdis.slave.Slave 2 %s /tmp/jcast/sorted_map/%s /tmp/jcast/reduced_map/%s";
    private final static String COLLECT_TEMPLATE = UNCHECKED_SCP + "%s@%s:/tmp/jcast/reduced_map/%s /tmp/jcast/reduced_map/";

    public static void main(String[] args) throws InterruptedException, IOException {
        new Master().exec(args);
    }

    private void exec(String[] args) throws InterruptedException, IOException {

        if (args.length < 1) {
            logger.error("Not enough argument provided, missing mapper class");
            System.exit(1);
        }

        String mapperClass = args[0];
        String inputFile = args[1];
        ArrayList<String> hosts;
        if (args.length >= 3) {
            hosts = getValidHosts(args[2]);
        } else {
            hosts = getValidHosts(Master.class.getResourceAsStream("/hosts"));
        }

        if(hosts.isEmpty()){
            logger.error("There is no availible hosts.");
            System.exit(4);
        }

        ConfUtils.loadConf(Master.class.getResourceAsStream("/config.properties"));

        FilesSplitUtils.splitFile(ConfUtils.getSplitsNbr(), inputFile, ConfUtils.getSplitsDir());

        List<Task> taskList = IntStream.range(0, ConfUtils.getSplitsNbr()).mapToObj(index ->
                new Task.TaskBuilder(index)
                        .setHostname(hosts.get(index % hosts.size())) // Set Hostname from hostname list
                        .setInputUri(String.format(ConfUtils.getSplitsDir() + ConfUtils.getSplitsTemplate(), index))
                        .setOutputUri("")
                        .build()).collect(Collectors.toList());

        HashMap<String, Task> umsTasks = new HashMap<>();
        ConcurrentHashMap<String, Set<String>> keysUms = new ConcurrentHashMap<>();
        HashMap<String, HostStat> hostStats = new HashMap<>();

        taskList.forEach(t -> hostStats.put(t.getHostname(), new HostStat(t.getHostname())));

        boolean frs = taskList.parallelStream().map(task -> {
            String splitPath = String.format(ConfUtils.getSplitsDir() + ConfUtils.getSplitsTemplate(), task.getIndex());
            String splitName = String.format("S%d.txt", task.getIndex());
            logger.info("copying " + splitPath + " to " + task.getHostname());
            String mkdirCommand = String.format(SPLIT_MKDIR_TEMPLATE, ConfUtils.getUsername(), task.getHostname());

            String scpCommand = String.format(SPLIT_COPY_TEMPLATE, splitPath, ConfUtils.getUsername(), task.getHostname());

            String mapCmd = String.format(MAPPER_TEMPLATE, ConfUtils.getUsername(), task.getHostname(), ConfUtils.getJarPath(), mapperClass, splitName);

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
            umsTasks.put(umx, task);

            boolean mapRet = ProcessUtils.consumeCommand(mapCmd, 3000, l -> {
                System.out.println(task.getHostname() + " " + l);
                keysUms.putIfAbsent(l, new HashSet<>());
                keysUms.get(l).add(umx);
            });

            if (mapRet) {
                logger.info(String.format("UM%d - %s", task.getIndex(), task.getHostname()));
                hostStats.get(task.getHostname()).addMap();
            }

            return mapRet;
        }).reduce((a, b) -> a && b).orElse(false);
        logger.info("Final result: " + frs);

        for (String s : keysUms.keySet()) {
            logger.info(s + " - <" + keysUms.get(s).stream().reduce((a, b) -> a + "," + b) + ">");
        }

        ArrayList<ShuffleTask> shuffleTasks = new ArrayList<>();

        Set<CopyTask> copyTasks = keysUms.entrySet().stream().flatMap(entry -> {
            String key = entry.getKey();
            String[] ums = entry.getValue().toArray(new String[0]);
            if (ums.length < 2) {
                if (ums.length == 1) {
                    // No shuffle to do
                    String hostname = umsTasks.get(ums[0]).getHostname();
                    ShuffleTask shuffleTask = new ShuffleTask(key, hostname);
                    shuffleTask.addUm(ums[0]);
                    shuffleTasks.add(shuffleTask);
                    hostStats.get(hostname).addKey(key);
                } else {
                    // Nothing to do
                    logger.fatal("Found empty ums list for key: " + key);
                }
                // return empty stream, no copy to do
                return Stream.empty();

            } else {
                String firstUm = ums[0];
                String dstHost = umsTasks.get(firstUm).getHostname();
                // assign shuffle task to first ums host
                ShuffleTask shuffleTask = new ShuffleTask(key, dstHost);
                shuffleTasks.add(shuffleTask);
                shuffleTask.addUm(firstUm);
                hostStats.get(dstHost).addKey(key);

                return Arrays.stream(ums).skip(1).map(um -> {
                    String srcHost = umsTasks.get(um).getHostname();
                    shuffleTask.addUm(um);
                    return new CopyTask(key, dstHost, srcHost, um);
                });
            }
        }).collect(Collectors.toSet());

        logger.info("Out of " + keysUms.size() + " keys there is " + copyTasks.size() + " copy task.");

        boolean copyResult = copyTasks.stream().parallel().map(
                copyTask -> {
                    String copyCommand = String.format(UM_COPY_TEMPLATE, ConfUtils.getUsername(), copyTask.getSrcHost(),
                            copyTask.getUmFile(), ConfUtils.getUsername(), copyTask.getDstHost());
                    boolean rs = ProcessUtils.runCommand(copyCommand, 60);
                    if (!rs) {
                        logger.error("command " + copyCommand + "failed.");
                    }
                    return new Pair<>(copyCommand, rs);
                }
        ).filter(Pair::getValue).map(Pair::getKey)
                .map(ProcessUtils::runCommand)
                .reduce((a, b) -> a && b).orElse(false);

        if (!copyResult) {
            logger.error("Copy failed, abort.");
            System.exit(2);
        }

        // When all copies finished, start shuffle
        Stream<ReduceTask> reduceTaskStream = shuffleTasks.stream().parallel().map(shuffleTask -> {
            String ums = shuffleTask.getUms("/tmp/jcast/unsorted_map");
            String shuffleCmd = String.format(SHUFFLER_TEMPLATE, ConfUtils.getUsername(), shuffleTask.getHostname(),
                    ConfUtils.getJarPath(), shuffleTask.getKey(), "/tmp/jcast/sorted_map/" + shuffleTask.getSm(), ums);

            boolean rs = ProcessUtils.runCommand(shuffleCmd, 60);
            if (!rs) {
                logger.error("command " + shuffleCmd + "failed.");
            }
            hostStats.get(shuffleTask.getHostname()).addShuffle();
            return ReduceTask.getFromShuffleTask(shuffleTask);
        });

        // Prepare output
        if (!ProcessUtils.runCommand("mkdir -p /tmp/jcast/reduced_map", 60)) {
            logger.error("Failed to create directory /tmp/jcast/reduced_map");
            System.exit(3);
        }

        // When shuffle task is finished start immediately Reduce
        boolean reduceResult = reduceTaskStream.map(reduceTask -> {
            String reduceCmd = String.format(REDUCER_TEMPLATE, ConfUtils.getUsername(), reduceTask.getHostname(),
                    ConfUtils.getJarPath(), reduceTask.getKey(), reduceTask.getSm(), reduceTask.getRm());

            boolean rs = ProcessUtils.runCommand(reduceCmd, 60);
            if (!rs) {
                logger.error("command " + reduceCmd + "failed.");
            }
            hostStats.get(reduceTask.getHostname()).addReduce();
            return new Pair<>(reduceTask, rs);
        }).map(p -> {
            if (p.getValue()) {
                String collectCmd = String.format(COLLECT_TEMPLATE, ConfUtils.getUsername(), p.getKey().getHostname(), p.getKey().getRm());
                boolean collectRs = ProcessUtils.runCommand(collectCmd, 10);
                if (collectRs) {
                    try {
                        Files.lines(Paths.get("/tmp/jcast/reduced_map/" + p.getKey().getRm())).forEach(System.out::println);
                        return true;
                    } catch (IOException e) {
                        logger.error("Failed to read RM file: " + p.getKey().getRm());
                    }
                }
            }
            return false;
        }).reduce((a, b) -> a && b).orElse(false);


        logger.info("Reduce finished successfuly: " + reduceResult);

        for (HostStat hostStat : hostStats.values()) {
            logger.info(hostStat.getStats());
        }

        for (HostStat hostStat : hostStats.values()) {
            logger.info(hostStat.getHostname() + ": " + hostStat.getKeys());
        }

        ProcessUtils.shutdown();
    }


}