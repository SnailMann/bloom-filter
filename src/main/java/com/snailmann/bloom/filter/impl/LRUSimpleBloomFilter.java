package com.snailmann.bloom.filter.impl;

import com.snailmann.bloom.filter.BaseLRUBloomFilter;
import com.snailmann.bloom.filter.FilterConfiguration;
import com.snailmann.bloom.filter.LRUFilterConfiguration;
import com.snailmann.bloom.filter.type.LRU;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liwenjie
 */
@Slf4j
public class LRUSimpleBloomFilter extends BaseLRUBloomFilter<String> implements LRU {

    private AtomicInteger version = new AtomicInteger(0);

    private List<SimpleBloomFilter> filters;

    public LRUSimpleBloomFilter() {
        this(null, LRUFilterConfiguration.defaultConfiguration());
    }

    public LRUSimpleBloomFilter(String tag, LRUFilterConfiguration configuration) {
        super(tag, configuration);
        // filters
        this.filters = new ArrayList<>(configuration.getMaxSize());
        FilterConfiguration templateConfiguration = configuration.getTemplateConfiguration();
        for (int i = 0; i < configuration.getSize(); i++) {
            this.filters.add(SimpleBloomFilter
                    .create(String.valueOf(version.getAndIncrement()), templateConfiguration));
        }
    }

    @Override
    public synchronized void put(String obj) {
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
            int itemSize = filter.getCurrentSize();
            if ((double) itemSize >= filter.configuration().getN()) {
                if (size >= configuration.getMaxSize()) {
                    log.info("remove");
                    filters.remove(0);
                }
                log.info("new");
                FilterConfiguration c = FilterConfiguration.copy(filter.configuration());
                var newFilter = newFilter(c);
                filters.add(newFilter);
            }
        }
        var filter = filters.get(size - 1);
        filter.put(obj);
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

    private SimpleBloomFilter newFilter(FilterConfiguration configuration) {
        return SimpleBloomFilter.create(String.valueOf(version.getAndIncrement()), configuration);
    }

    private void expired() {
        long timestamp = System.currentTimeMillis();
        filters.removeIf(filter -> {
            var u = filter.configuration().getMeta().getUpdateDate().getTime();
            var ttl = configuration.getTtl().toMillis();
            // Note: Do not compare "ttl + u < timestamp", which will cause long overflow
            if (timestamp - u > ttl) {
                log.info("remove filter {}", filter);
                return true;
            }
            return false;
        });
    }

    public static LRUSimpleBloomFilter create() {
        return new LRUSimpleBloomFilter();
    }

    public static LRUSimpleBloomFilter create(LRUFilterConfiguration configuration) {
        return new LRUSimpleBloomFilter(null, configuration);
    }

    public static LRUSimpleBloomFilter create(String tag, LRUFilterConfiguration configuration) {
        return new LRUSimpleBloomFilter(tag, configuration);
    }

    public static LRUSimpleBloomFilter create(String tag, int sn, double sfpp, int maxSize) {
        return new LRUSimpleBloomFilter(tag, LRUFilterConfiguration.config(sn, sfpp, maxSize));
    }

    public static LRUSimpleBloomFilter create(String tag, int sn, double sfpp, int maxSize, Duration ttl) {
        return new LRUSimpleBloomFilter(tag, LRUFilterConfiguration.config(sn, sfpp, maxSize, ttl));
    }

    @SneakyThrows
    public static void main(String[] args) {
        LRUSimpleBloomFilter boomFilter = LRUSimpleBloomFilter.create("test", 150, 0.001, 3, Duration.ofSeconds(1));
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
        for (int i = 0; i < 200; i++) {
            Thread.sleep(100);
            System.out.println(i);
            boomFilter.put(i + "");
        }

        for (int i = 0; i < 200; i++) {
            boolean flag = boomFilter.mightContains(i + "");
            System.out.println(i + ":" + flag);
        }
    }
}
