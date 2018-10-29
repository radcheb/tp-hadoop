package com.radcheb.sysdis.example.wordcount;

import com.radcheb.sysdis.utils.Pair;

import java.util.function.BinaryOperator;

public class AdditionReducer implements BinaryOperator<Pair<String, Long>> {
    @Override
    public Pair<String, Long> apply(Pair<String, Long> p1, Pair<String, Long> p2) {
        return new Pair<>(p1.getKey(), p1.getValue() + p2.getValue());
    }
}
