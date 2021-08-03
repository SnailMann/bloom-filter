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
public class LRUBloomFilter<E> extends BaseLRUFilter<E> {

    private AtomicInteger v = new AtomicInteger(0);
    private List<BloomFilter<E>> filters;

    private LRUBloomFilter() {
        this(null, LRUFilterConfig.defaultConfig());
    }

    private LRUBloomFilter(String name, LRUFilterConfig config) {
        super(name, config);
        this.filters = new ArrayList<>(config.getMaxSize());
        FilterConfig template = config.getTemplateConfig();
        for (int i = 0; i < config.getSize(); i++) {
            String childName = String.valueOf(v.incrementAndGet());
            this.filters.add(BloomFilter.create(childName, template));
        }
    }

    @Override
    public void put(E element) {
        byte[] bs = element.toString().getBytes(charset());
        put(bs);
    }

    @Override
    public synchronized void put(byte[] bs) {
        // remove expired filter
        removeInvaild();

        // initialize filter
        if (CollectionUtils.isEmpty(filters)) {
            log.info("new filter");
            FilterConfig c = FilterConfig.copyOf(this.config.templateConfig);
            var newFilter = newFilter(c);
            filters.add(newFilter);
        }
        // filter
        int size = filters.size();
        if (config.getMaxSize() > 1) {
            var filter = filters.get(size - 1);
            if (filter.getCurrentSize() >= filter.config().getN()) {
                if (size >= config.getMaxSize()) {
                    filters.remove(0);
                }
                FilterConfig config = FilterConfig.copyOf(filter.config());
                var newFilter = newFilter(config);
                filters.add(newFilter);
            }
        }
        var filter = filters.get(size - 1);
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
    public boolean mightContains(E element) {
        byte[] bs = element.toString().getBytes(charset());
        return mightContains(bs);
    }

    @Override
    public synchronized boolean mightContains(byte[] bs) {
        if (CollectionUtils.isEmpty(filters)) {
            return false;
        }
        for (var filter : filters) {
            boolean flag = filter.mightContains(bs);
            if (flag) {
                return true;
            }
        }
        return false;
    }

    private void removeInvaild() {
        long timestamp = System.currentTimeMillis();
        filters.removeIf(filter -> {
            var u = filter.config().getModifyDate();
            var ttl = config.getTtl().toMillis();
            // Note: Do not compare "ttl + u < timestamp", which will cause long overflow
            return timestamp - u > ttl;
        });
    }

    private BloomFilter<E> newFilter(FilterConfig configuration) {
        return BloomFilter.create(String.valueOf(v.getAndIncrement()), configuration);
    }

    public static <R> LRUBloomFilter<R> create() {
        return new LRUBloomFilter<>();
    }

    public static <R> LRUBloomFilter<R> create(String name, LRUFilterConfig configuration) {
        return new LRUBloomFilter<>(name, configuration);
    }

    public static <R> LRUBloomFilter<R> create(String name, int sn, double sfpp, int maxSize) {
        return new LRUBloomFilter<>(name, LRUFilterConfig.config(sn, sfpp, maxSize));
    }

    public static <R> LRUBloomFilter<R> create(String name, int sn, double sfpp, int maxSize, Duration ttl) {
        return new LRUBloomFilter<>(name, LRUFilterConfig.config(sn, sfpp, maxSize, ttl));
    }

}
