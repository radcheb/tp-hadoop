package com.radcheb.sysdis.utils;

import java.io.Serializable;

public class Pair<T, E> implements Serializable {

    private T key;
    private E value;

    public Pair(T key, E value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public E getValue() {
        return value;
    }
}
