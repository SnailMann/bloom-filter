package com.snailmann.bloom.filter.config;

import lombok.Data;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Duration;

import static com.snailmann.bloom.utils.BloomUtils.*;

/**
 * Bloom filter configuration with LRU support
 *
 * @author liwenjie
 */
@Data
public class LRUFilterConfig {

    public static final Duration NEVER_EXPIRED = Duration.ofMillis(Long.MAX_VALUE);
    private static final int FILTER_INITIAL_SIZE = 1;
    private static final int FILTER_MAX_SIZE = 32;

    /**
     * A byte has 8 bits
     */
    public static final int B = 8;

    /**
     * Current number of filters
     */
    private int size = FILTER_INITIAL_SIZE;

    /**
     * Max number of filters
     */
    private int maxSize;

    /**
     * Series false positive
     */
    private double sfpp;

    /**
     * ttl
     */
    private Duration ttl;

    /**
     * Template Config of filter
     */
    public FilterConfig templateConfig;

    public static LRUFilterConfig defaultConfig() {
        return config(10000, 0.001d, 3);
    }

    public static LRUFilterConfig config(int sn, double sfpp, int maxSize) {
        return config(sn, sfpp, maxSize, NEVER_EXPIRED);
    }

    public static LRUFilterConfig config(int sn, double sfpp, int maxSize, Duration ttl) {
        LRUFilterConfig configuration = new LRUFilterConfig();
        configuration.setSize(FILTER_INITIAL_SIZE);
        configuration.setMaxSize(maxSize);
        configuration.setSfpp(sfpp);
        configuration.setTtl(ttl);

        FilterConfig template = optimalConfigOfFilter(sn, sfpp, maxSize);
        configuration.setTemplateConfig(template);
        return configuration;
    }


    /**
     * Find the optimal configuration of a single filter according to the total N, the series fpp and the max number of filters you want to support
     * <p>
     * the formula of series fpp :
     * 1. sfpp = 1 - (1 - p)^size
     * 2. p = 1 - Math.pow(1 - sfpp, (double) 1 / size)
     *
     * @param sn      total n of series filters
     * @param sfpp    fpp of series filters
     * @param maxSize max number of filters
     * @return filter configuration
     */
    public static FilterConfig optimalConfigOfFilter(int sn, double sfpp, int maxSize) {
        Assert.isTrue(maxSize >= FILTER_INITIAL_SIZE && maxSize <= FILTER_MAX_SIZE,
                "maxSize needs to be in the range of FILTER_INITAL_SIZE to FILTER_MAX_SIZE");
        Assert.isTrue(sn > 0, "series n must be more than 0");

        double p = 1 - Math.pow(1 - sfpp, 1d / maxSize);
        int n = sn / maxSize;
        return FilterConfig.config(n, p);
    }

    /**
     * Method of testing optimum value
     *
     * @param sn   total n of series filters
     * @param sfpp fpp of series filters
     */
    private static void optimal(int sn, double sfpp) {
        for (int size = FILTER_INITIAL_SIZE; size <= FILTER_MAX_SIZE; size++) {
            double p = 1 - Math.pow(1 - sfpp, 1d / size);
            int n = sn / size;
            int m = optimalNumOfBits(n, p);
            int k = optimalNumOfHashFunctions(n, m);
            System.out.println(
                    BigDecimal.valueOf(p) + ":k=" + k + ":n=" + n + ":m=" + m
                            + ":count=" + size + ":b=" + bitsOfElement(n, m));
        }
    }
}
