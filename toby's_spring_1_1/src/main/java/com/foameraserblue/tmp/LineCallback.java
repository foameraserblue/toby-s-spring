package com.foameraserblue.tmp;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
