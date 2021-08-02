package com.snailmann.bloom.filter.basic;

import com.snailmann.bloom.filter.config.LRUFilterConfiguration;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liwenjie
 */
public abstract class BaseLRUBloomFilter<T> implements BloomFilter<T> {

    @Getter
    @Setter
    private String name = "default";

    public LRUFilterConfiguration configuration;

    public BaseLRUBloomFilter() {
        this(null, LRUFilterConfiguration.defaultConfiguration());
    }

    public BaseLRUBloomFilter(String name, LRUFilterConfiguration configuration) {
        if (null != name) {
            this.name = name;
        }
        this.configuration = configuration;
    }

}
