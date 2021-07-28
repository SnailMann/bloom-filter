package com.snailmann.bloom.filter.impl;

import com.snailmann.bloom.filter.BaseLRUBloomFilter;
import com.snailmann.bloom.filter.config.FilterConfiguration;
import com.snailmann.bloom.filter.config.LRUFilterConfiguration;
import com.snailmann.bloom.filter.type.LRU;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liwenjie
 */
@Slf4j
public class LRUBloomFilter extends BaseLRUBloomFilter<String> implements LRU {

    private AtomicInteger version = new AtomicInteger(0);

    private List<BloomFilter> filters;

    public LRUBloomFilter() {
        this(null, LRUFilterConfiguration.defaultConfiguration());
    }

    public LRUBloomFilter(String tag, LRUFilterConfiguration configuration) {
        super(tag, configuration);
        // filters
        this.filters = new ArrayList<>(configuration.getMaxSize());
        FilterConfiguration templateConfiguration = configuration.getTemplateConfiguration();
        for (int i = 0; i < configuration.getSize(); i++) {
            this.filters.add(BloomFilter
                    .create(String.valueOf(version.getAndIncrement()), templateConfiguration));
        }
    }

    @Override
    public synchronized void put(String obj) {
        put(obj.getBytes());
    }

    @Override
    public void put(byte[] bs) {
        // remove expire filter
        expired();
        // initialize filter
        if (CollectionUtils.isEmpty(filters)) {
            log.info("new filter");
            FilterConfiguration c = FilterConfiguration.copy(this.configuration.templateConfiguration);
            var newFilter = newFilter(c);
            filters.add(newFilter);
        }
        // filter
        int size = filters.size();
        if (configuration.getMaxSize() > 1 && size <= configuration.getMaxSize()) {
            var filter = filters.get(size - 1);
            int elementSize = filter.getCurrentSize();
            if ((double) elementSize >= filter.configuration().getN()) {
                if (size >= configuration.getMaxSize()) {
                    filters.remove(0);
                }
                FilterConfiguration c = FilterConfiguration.copy(filter.configuration());
                var newFilter = newFilter(c);
                filters.add(newFilter);
            }
        }
        var filter = filters.get(size - 1);
        filter.put(bs);
    }

    @Override
    public void putAll(List<String> objs) {

    }

    @Override
    public synchronized boolean mightContains(String obj) {
        if (CollectionUtils.isEmpty(filters)) {
            return false;
        }
        for (var filter : filters) {
            boolean flag = filter.mightContains(obj);
            if (flag) {
                return true;
            }
        }
        return false;
    }

    private BloomFilter newFilter(FilterConfiguration configuration) {
        return BloomFilter.create(String.valueOf(version.getAndIncrement()), configuration);
    }

    private void expired() {
        long timestamp = System.currentTimeMillis();
        filters.removeIf(filter -> {
            var u = filter.configuration().getMeta().getUpdateDate().getTime();
            var ttl = configuration.getTtl().toMillis();
            // Note: Do not compare "ttl + u < timestamp", which will cause long overflow
            if (timestamp - u > ttl) {
                return true;
            }
            return false;
        });
    }

    public static LRUBloomFilter create() {
        return new LRUBloomFilter();
    }

    public static LRUBloomFilter create(LRUFilterConfiguration configuration) {
        return new LRUBloomFilter(null, configuration);
    }

    public static LRUBloomFilter create(String tag, LRUFilterConfiguration configuration) {
        return new LRUBloomFilter(tag, configuration);
    }

    public static LRUBloomFilter create(String tag, int sn, double sfpp, int maxSize) {
        return new LRUBloomFilter(tag, LRUFilterConfiguration.config(sn, sfpp, maxSize));
    }

    public static LRUBloomFilter create(String tag, int sn, double sfpp, int maxSize, Duration ttl) {
        return new LRUBloomFilter(tag, LRUFilterConfiguration.config(sn, sfpp, maxSize, ttl));
    }

}
