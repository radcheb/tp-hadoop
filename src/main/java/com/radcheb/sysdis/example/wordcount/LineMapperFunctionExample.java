package com.radcheb.sysdis.example.wordcount;

import com.radcheb.sysdis.slave.mapper.LineMapperFunction;
import com.radcheb.sysdis.utils.Pair;

import java.util.Arrays;
import java.util.stream.Stream;

public class LineMapperFunctionExample implements LineMapperFunction<Integer> {

    public Stream<Pair<String, Integer>> mapToLine(String line) {
        return Arrays.stream(line.split(" ")).map(w -> new Pair(w, 1));
    }
}
