package com.snailmann.bloom.filter.basic;

import com.snailmann.bloom.filter.config.FilterConfig;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liwenjie
 */
public abstract class BaseFilter<T> implements Filter<T> {


    /**
     * A byte has 8 bits
     */
    protected static final int B = 8;

    /**
     * B mask
     */
    protected static final int B_MASK = B - 1;

    /**
     * Tag of Bloom Filter
     */
    private String name;

    /**
     * Bloom Filter configuration
     */
    private FilterConfig config;

    public BaseFilter() {
        this(null, FilterConfig.defaultConfig());
    }

    public BaseFilter(String name, FilterConfig config) {
        this.name = StringUtils.isBlank(name) ? DEFAULT_NAME : name;
        this.config = config;
    }

    /**
     * Get configuration
     *
     * @return configuration of filter
     */
    public FilterConfig config() {
        return this.config;
    }

    /**
     * Get name of filter
     *
     * @return name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Get the total bits of the filter
     *
     * @return m
     */
    public int bitsOfFilter() {
        return config().getM();
    }

    /**
     * Get the average bits occupied by each element
     *
     * @return c
     */
    public double bitsEachElement() {
        return config().getC();
    }

    /**
     * Get the number of elements expected to be supported by the filter
     *
     * @return n
     */
    public int numOfExpectedElements() {
        return config().getN();
    }

    /**
     * Get fpp of filter
     *
     * @return p
     */
    public double fpp() {
        return config().getP();
    }

    /**
     * Get the number of hash fucntions
     *
     * @return k
     */
    public int numOfHashFunctions() {
        return config().getK();
    }

}
