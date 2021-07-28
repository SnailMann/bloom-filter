package com.snailmann.bloom.filter;

import com.snailmann.bloom.filter.config.LRUFilterConfiguration;
import com.snailmann.bloom.filter.type.LRU;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liwenjie
 */
public abstract class BaseLRUBloomFilter<T> implements BloomFilter<T>, LRU {

    @Getter
    @Setter
    private double threshold = 0.90d;

    @Getter
    @Setter
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

}
