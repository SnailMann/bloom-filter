package com.snailmann.bloom.filter.basic;

import java.util.List;

/**
 * @author liwenjie
 */
public interface Filter<T> {

    String DEFAULT_NAME = "default";

    /**
     * Put a element to the filter
     *
     * @param element element want to put
     */
    void put(T element);

    /**
     * Put a element to the filter
     *
     * @param bytes raw bytes of element want to put
     */
    void put(byte[] bytes);

    /**
     * Performs a bulk put operation for a collection elements
     *
     * @param elements a collection element
     */
    void putAll(List<T> elements);

    /**
     * Whether an element in present in the filter
     *
     * @param element element want to know
     * @return {@code true} if the element present in the filter
     */
    boolean mightContains(T element);

    /**
     * Whether an element in present in the filter
     *
     * @param bytes raw bytes of element
     * @return {@code true} if the element present in the filterz
     */
    boolean mightContains(byte[] bytes);

    /**
     * Get name of filter
     *
     * @return name
     */
    default String name() {
        return DEFAULT_NAME;
    }

}
