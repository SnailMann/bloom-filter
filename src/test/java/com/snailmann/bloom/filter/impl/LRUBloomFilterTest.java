package com.snailmann.bloom.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author liwenjie
 */
@Slf4j
class LRUBloomFilterTest {

    @Test
    public void fpp_test() {
        var n = 30000;
        var p = 0.001;
        var msize = 10;
        LRUBloomFilter<Integer> filter = LRUBloomFilter.create("test", n, p, msize);
        var fpp = test(filter, n);
        Assert.isTrue(String.format("%.3f", p).equals(String.format("%.3f", fpp)), "fpp not match");
    }

    private double test(LRUBloomFilter<Integer> filter, int n) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
        Map<String, Boolean> map = new LinkedHashMap<>();
        System.out.println(filter.configuration.templateConfiguration);

        for (int i = 0; i < n; i++) {
            int index = i;
            executor.execute(() -> filter.put(index));
        }
        for (int i = n; i < n * 2; i++) {
            boolean res = filter.mightContains(i);
            map.put(i + "", res);
        }

        var m = map.values().stream().collect(Collectors.partitioningBy(bool -> bool));
        int fppNum = m.getOrDefault(true, Collections.emptyList()).size();
        int tppNum = m.getOrDefault(false, Collections.emptyList()).size();
        var fpp = (double) fppNum / n;

        System.out.println(String.format("total: %s, false: %s, true: %s, fpp: %s", map.size(), fppNum, tppNum, BigDecimal.valueOf(fpp)));
        return fpp;

    }


}