package com.snailmann.bloom.filter;

import java.util.List;

/**
 * @author liwenjie
 */
public interface BloomFilter<T> {

    void put(T obj);

    void put(byte[] bytes);

    void putAll(List<T> objs);

    boolean mightContains(T obj);

}
