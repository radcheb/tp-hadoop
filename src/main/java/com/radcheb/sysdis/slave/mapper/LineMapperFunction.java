package com.radcheb.sysdis.slave;

import com.radcheb.sysdis.utils.Pair;

import java.io.Serializable;
import java.util.stream.Stream;

@FunctionalInterface
public interface LineMapperFunction<T extends Serializable> {

    Stream<Pair<String, T>> mapToLine(String line);

}
