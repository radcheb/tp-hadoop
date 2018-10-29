package com.radcheb.sysdis.metrics;

import java.util.ArrayList;
import java.util.HashSet;

public class HostStat {

    private final static String STATS_TEMPLATE = "%s: %02d keys - %02d maps - %02d shuffles - %02d reduces";

    private final String hostname;
    private final HashSet<String> keys;
    private int maps = 0;
    private int shuffles = 0;
    private int reduces = 0;


    public HostStat(String hostname) {
        this.hostname = hostname;
        this.keys = new HashSet<>();
    }

    public void addKey(String key){
        this.keys.add(key);
    }

    public void addMap(){
        this.maps +=1;
    }

    public void addShuffle(){
        this.shuffles +=1;
    }

    public void addReduce(){
        this.reduces +=1;
    }

    public String getStats(){
        return String.format(STATS_TEMPLATE, this.hostname, keys.size(), maps, shuffles, reduces);
    }

    public String getHostname() {
        return hostname;
    }

    public String getKeys(){
        return String.join(", ", keys);
    }
}
