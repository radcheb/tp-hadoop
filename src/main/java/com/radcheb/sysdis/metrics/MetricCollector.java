package com.radcheb.sysdis.metrics;

import java.util.ArrayList;
import java.util.HashMap;

public class MetricCollector {

    private final HashMap<String, ArrayList<Float>> mapTimes;
    private final HashMap<String, ArrayList<Float>> shuffleTimes;
    private final HashMap<String, ArrayList<Float>> reduceTimes;

    public MetricCollector(HashMap<String, ArrayList<Float>> mapTimes, HashMap<String, ArrayList<Float>> shuffleTimes, HashMap<String, ArrayList<Float>> reduceTimes) {
        this.mapTimes = mapTimes;
        this.shuffleTimes = shuffleTimes;
        this.reduceTimes = reduceTimes;
    }
}
