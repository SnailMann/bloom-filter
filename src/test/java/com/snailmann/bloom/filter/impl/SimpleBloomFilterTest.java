package com.snailmann.bloom.filter.impl;

import com.snailmann.bloom.filter.impl.SimpleBloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liwenjie
 */
@Slf4j
public class SimpleBloomFilterTest {

    @Test
    public void fpp_n_p_test() {
        var n = 30000;
        var p = 0.001;
        SimpleBloomFilter filter = SimpleBloomFilter.create("test", n, p);
        var fpp = test(filter, n);
        Assert.isTrue(String.format("%.3f", p).equals(String.format("%.3f", fpp)), "fpp not match");
    }

    @Test
    public void fpp_n_k_b_test() {
        var n = 30000;
        var k = 10;
        var b = 14.377333333333333;
        var p = 0.001;
        SimpleBloomFilter filter = SimpleBloomFilter.create("test", n, k, b);
        var fpp = test(filter, n);
        Assert.isTrue(String.format("%.3f", p).equals(String.format("%.3f", fpp)), "fpp not match");
    }

    public double test(SimpleBloomFilter filter, int n) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        System.out.println(filter);

        for (int i = 0; i < n; i++) {
            filter.put(i + "");
        }
        for (int i = n; i < n * 2; i++) {
            boolean res = filter.mightContains(i + "");
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