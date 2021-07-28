package com.snailmann.bloom.filter;

import com.snailmann.bloom.filter.config.FilterConfiguration;

/**
 * @author liwenjie
 */
public abstract class BaseBloomFilter<T> implements BloomFilter<T> {

    /**
     * A byte has 8 bits
     */
    public static final int B = 8;

    /**
     * Tag of Bloom Filter
     */
    private String tag = "default";

    /**
     * Bloom Filter configuration
     */
    protected FilterConfiguration configuration;

    public BaseBloomFilter() {
        this(null, FilterConfiguration.defaultConfiguration());
    }

    public BaseBloomFilter(String tag, FilterConfiguration configuration) {
        if (tag != null) {
            this.tag = tag;
        }
        this.configuration = configuration;
    }

    public FilterConfiguration configuration() {
        return this.configuration;
    }

    public long bitsOfFilter() {
        return this.configuration.getM();
    }

    public double bitsPerElement() {
        return this.configuration.getB();
    }

    public int numOfExpectedElement() {
        return this.configuration.getN();
    }

    public double fpp() {
        return configuration.getP();
    }

    public int numOfHashFunctions() {
        return configuration.getK();
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
