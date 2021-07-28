package com.snailmann.bloom.filter;

import com.snailmann.bloom.filter.config.LRUFilterConfiguration;
import com.snailmann.bloom.filter.type.LRU;

/**
 * @author liwenjie
 */
public abstract class BaseLRUBloomFilter<T> implements BloomFilter<T>, LRU {

    private double threshold = 0.90d;

    private String tag = "default";

    public LRUFilterConfiguration configuration;

    public BaseLRUBloomFilter() {
        this(null, LRUFilterConfiguration.defaultConfiguration());
    }

    public BaseLRUBloomFilter(String tag, LRUFilterConfiguration configuration) {
        if (null != tag) {
            this.tag = tag;
        }
        this.configuration = configuration;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
