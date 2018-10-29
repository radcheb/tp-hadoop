package com.radcheb.sysdis.utils.reduce;

import com.radcheb.sysdis.utils.shuffle.ShuffleTask;

public class ReduceTask {

    private final int index;
    private final String key;
    private final String hostname;
    private final String rm;
    private final String sm;

    public ReduceTask(int index, String key, String host, String sm) {
        this.key = key;
        this.hostname = host;
        this.sm = sm;
        this.index = index;
        this.rm = "RM" + index;
    }

    public String getKey() {
        return key;
    }

    public String getHostname() {
        return hostname;
    }

    public int getIndex() {
        return index;
    }

    public String getRm() {
        return rm;
    }

    public String getSm() {
        return sm;
    }

    public static ReduceTask getFromShuffleTask(ShuffleTask shuffleTask){
        return new ReduceTask(shuffleTask.getIndex(), shuffleTask.getKey(), shuffleTask.getHostname(), shuffleTask.getSm());
    }
}
