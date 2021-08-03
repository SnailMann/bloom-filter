package com.snailmann.bloom.filter.basic;

import com.snailmann.bloom.filter.config.LRUFilterConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liwenjie
 */
public abstract class BaseLRUBloomFilter<T> implements BloomFilter<T> {

    @Getter
    @Setter
    private String name = "default";

    public LRUFilterConfig configuration;

    public BaseLRUBloomFilter() {
        this(null, LRUFilterConfig.defaultConfiguration());
    }

    public BaseLRUBloomFilter(String name, LRUFilterConfig configuration) {
        if (null != name) {
            this.name = name;
        }
        this.configuration = configuration;
    }

}
