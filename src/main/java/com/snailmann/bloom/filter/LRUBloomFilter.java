package com.snailmann.bloom.filter;

import com.snailmann.bloom.filter.basic.BaseLRUFilter;
import com.snailmann.bloom.filter.config.FilterConfig;
import com.snailmann.bloom.filter.config.LRUFilterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.snailmann.bloom.filter.config.FilterConfig.charset;

/**
 * @author liwenjie
 */
@Slf4j
public class LRUFilter<E> extends BaseLRUFilter<E> {

    private AtomicInteger version = new AtomicInteger(0);

    private List<BloomFilter<E>> bloomFilters;

    private LRUFilter() {
        this(null, LRUFilterConfig.defaultConfiguration());
    }

    private LRUFilter(String tag, LRUFilterConfig configuration) {
        super(tag, configuration);
        this.bloomFilters = new ArrayList<>(configuration.getMaxSize());
        FilterConfig templateConfiguration = configuration.getTemplateConfiguration();
        for (int i = 0; i < configuration.getSize(); i++) {
            this.bloomFilters.add(BloomFilter.create(String.valueOf(version.getAndIncrement()), templateConfiguration));
        }
    }

    @Override
    public synchronized void put(E element) {
        byte[] bs = element.toString().getBytes(charset());
        put(bs);
    }

    @Override
    public synchronized void put(byte[] bs) {
        // remove expire filter
        removeExpiredFilter();

        // initialize filter
        if (CollectionUtils.isEmpty(bloomFilters)) {
            log.info("new filter");
            FilterConfig c = FilterConfig.copyOf(this.configuration.templateConfiguration);
            var newFilter = newFilter(c);
            bloomFilters.add(newFilter);
        }
        // filter
        int size = bloomFilters.size();
        if (configuration.getMaxSize() > 1 && size <= configuration.getMaxSize()) {
            var filter = bloomFilters.get(size - 1);
            if (filter.getCurrentSize() >= filter.config().getN()) {
                if (size >= configuration.getMaxSize()) {
                    bloomFilters.remove(0);
                }
                FilterConfig c = FilterConfig.copyOf(filter.config());
                var newFilter = newFilter(c);
                bloomFilters.add(newFilter);
            }
        }
        var filter = bloomFilters.get(size - 1);
        filter.put(bs);
    }

    @Override
    public synchronized void putAll(List<E> elements) {
        for (E o : elements) {
            try {
                put(o);
            } catch (Exception e) {
                log.error("put a element [{}] error", o, e);
            }
        }
    }

    @Override
    public synchronized boolean mightContains(E element) {
        byte[] bs = element.toString().getBytes(charset());
        return mightContains(bs);
    }

    @Override
    public synchronized boolean mightContains(byte[] bs) {
        if (CollectionUtils.isEmpty(bloomFilters)) {
            return false;
        }
        for (var filter : bloomFilters) {
            boolean flag = filter.mightContains(bs);
            if (flag) {
                return true;
            }
        }
        return false;
    }

    private void removeExpiredFilter() {
        long timestamp = System.currentTimeMillis();
        bloomFilters.removeIf(filter -> {
            var u = filter.config().getMeta().getUpdateDate().getTime();
            var ttl = configuration.getTtl().toMillis();
            // Note: Do not compare "ttl + u < timestamp", which will cause long overflow
            return timestamp - u > ttl;
        });
    }

    private BloomFilter<E> newFilter(FilterConfig configuration) {
        return BloomFilter.create(String.valueOf(version.getAndIncrement()), configuration);
    }

    public static <R> LRUFilter<R> create() {
        return new LRUFilter<>();
    }

    public static <R> LRUFilter<R> create(LRUFilterConfig configuration) {
        return new LRUFilter<>(null, configuration);
    }

    public static <R> LRUFilter<R> create(String tag, LRUFilterConfig configuration) {
        return new LRUFilter<>(tag, configuration);
    }

    public static <R> LRUFilter<R> create(String tag, int sn, double sfpp, int maxSize) {
        return new LRUFilter<>(tag, LRUFilterConfig.config(sn, sfpp, maxSize));
    }

    public static <R> LRUFilter<R> create(String tag, int sn, double sfpp, int maxSize, Duration ttl) {
        return new LRUFilter<>(tag, LRUFilterConfig.config(sn, sfpp, maxSize, ttl));
    }

}
