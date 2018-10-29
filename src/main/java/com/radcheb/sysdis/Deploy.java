package com.radcheb.sysdis;

import com.radcheb.sysdis.etapes.etape7.Etape7_41;
import com.radcheb.sysdis.utils.ConfUtils;
import com.radcheb.sysdis.utils.Pair;
import com.radcheb.sysdis.utils.ProcessUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

import static com.radcheb.sysdis.etapes.etape7.Etape7_40.getValidHosts;

public class Deploy {

    private final static Logger logger = Logger.getLogger(Etape7_41.class);
    private final static String UNCHECKED_SSH = "ssh -q -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ";
    private final static String UNCHECKED_SCP = "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ";
    private final static String MKDIR_TEMPLATE = UNCHECKED_SSH + "%s@%s mkdir -p /tmp/jcast/data/jar /tmp/jcast/unsorted_map /tmp/jcast/sorted_map /tmp/jcast/reduced_map";
    private final static String COPY_TEMPLATE = UNCHECKED_SCP + "target/%s %s@%s:/tmp/jcast/data/jar/";

    public static void main(String[] args) throws InterruptedException {

        ConfUtils.loadConf(Deploy.class.getResourceAsStream("/config.properties"));

        ArrayList<String> hosts;
        if (args.length > 1) {
            hosts = getValidHosts(args[0]);
        } else {
            hosts = getValidHosts(Deploy.class.getResourceAsStream("/hosts"));
        }

        if(hosts.isEmpty()){
            logger.error("There is no availible hosts.");
            System.exit(4);
        }

        boolean finalRs = hosts.stream().parallel().map(h -> {
                String mkdirCommand = String.format(MKDIR_TEMPLATE, ConfUtils.getUsername(), h);
                String copyCommand = String.format(COPY_TEMPLATE, ConfUtils.getJarPath(), ConfUtils.getUsername(), h);
                boolean rs = (ProcessUtils.runCommand(mkdirCommand, 10) && ProcessUtils.runCommand(copyCommand, 100));
                return new Pair<>(h, rs);
        }).allMatch(Pair::getValue);

        logger.info("Deploy final status: " + finalRs);
    }
}
