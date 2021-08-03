package com.snailmann.bloom.filter.basic;

import com.snailmann.bloom.filter.config.LRUFilterConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liwenjie
 */
public abstract class BaseLRUFilter<T> implements Filter<T> {

    @Getter
    @Setter
    private String name;

    public LRUFilterConfig config;

    public BaseLRUFilter() {
        this(null, LRUFilterConfig.defaultConfig());
    }

    public BaseLRUFilter(String name, LRUFilterConfig config) {
        this.name = StringUtils.isBlank(name) ? DEFAULT_NAME : name;
        this.config = config;
    }

    public LRUFilterConfig config() {
        return this.config;
    }

}
