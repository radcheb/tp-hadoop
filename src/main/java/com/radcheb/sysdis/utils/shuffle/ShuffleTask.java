package com.radcheb.sysdis.utils.shuffle;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ShuffleTask {

    private final static AtomicInteger idsGen = new AtomicInteger();
    private final int index;
    private final String key;
    private final String sm;
    private final String hostname;
    private final ArrayList<String> ums;

    public ShuffleTask(String key, String hostname) {
        this.index = idsGen.incrementAndGet();
        this.key = key;
        this.sm = "SM" + index;
        this.hostname = hostname;
        this.ums = new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }

    public String getKey() {
        return key;
    }

    public String getSm() {
        return sm;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUms(String dir) {
        return ums.stream().map(um -> dir + "/" + um).collect(Collectors.joining(" "));
    }

    public void addUm(String um) {
        ums.add(um);
    }
}
