package com.snailmann.bloom.filter.basic;

import com.snailmann.bloom.filter.config.FilterConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liwenjie
 */
public abstract class BaseBloomFilter<T> implements BloomFilter<T> {

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
    @Getter
    @Setter
    private String name = "default";

    /**
     * Bloom Filter configuration
     */
    private FilterConfig config;

    public BaseBloomFilter() {
        this(null, FilterConfig.defaultConfig());
    }

    public BaseBloomFilter(String name, FilterConfig config) {
        if (name != null) {
            this.name = name;
        }
        this.config = config;
    }

    public FilterConfig config() {
        return this.config;
    }

    public long bitsOfFilter() {
        return this.config.getM();
    }

    public double bitsPerElement() {
        return this.config.getB();
    }

    public int numOfExpectedElement() {
        return this.config.getN();
    }

    public double fpp() {
        return config.getP();
    }

    public int numOfHashFunctions() {
        return config.getK();
    }
}
