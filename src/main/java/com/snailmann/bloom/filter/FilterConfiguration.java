package com.snailmann.bloom.filter;

import lombok.Data;

import java.util.Date;

import static com.snailmann.bloom.utils.BloomUtils.*;

/**
 * Bloom filter configuration
 *
 * @author liwenjie
 */
@Data
public class FilterConfiguration {

    /**
     * Number of items expected to be supported by the filter
     */
    private int n;

    /**
     * Number of filter bit buckets (ps: ot can be considered as the total size of the filter)
     * range: 1<= m <= Integer.MAX_VALUE, Why not long? Because of the Java language, the maximum data size is Integer.MAX_VALUE
     */
    private int m;

    /**
     * Number of hash functions
     */
    private int k;

    /**
     * Space occupied by unit item (unit: bit)
     */
    private double b;

    /**
     * Fpp (false positive probability)
     */
    private double p;

    /**
     * Configuration meta
     */
    private Meta meta;

    public FilterConfiguration() {
        this.meta = Meta.meta();
    }

    public static FilterConfiguration config(int n, int k, double b) {
        FilterConfiguration configuration = new FilterConfiguration();
        configuration.setN(n);
        configuration.setK(k);
        configuration.setB(b);
        configuration.setM(resizeNumOfBits(n, b));
        configuration.setP(optimalFpp(n, configuration.getM(), k));
        return configuration;
    }

    public static FilterConfiguration config(int n, double p) {
        FilterConfiguration configuration = new FilterConfiguration();
        configuration.setN(n);
        configuration.setP(p);
        int m = optimalNumOfBits(n, p);
        configuration.setM(m);
        configuration.setK(optimalNumOfHashFunctions(n, m));
        configuration.setB(bitsOfItem(n, m));
        return configuration;
    }

    public static FilterConfiguration defaultConfiguration() {
        return config(10000, 0.00046);
    }

    public static FilterConfiguration copy(FilterConfiguration src) {
        FilterConfiguration target = new FilterConfiguration();
        target.setN(src.getN());
        target.setM(src.getM());
        target.setP(src.getP());
        target.setK(src.getK());
        target.setB(src.getB());
        return target;
    }

    public Meta getMeta() {
        return meta;
    }

    @Data
    public static class Meta {
        Date createDate;
        Date updateDate;

        public static Meta meta() {
            Meta meta = new Meta();
            meta.setCreateDate(new Date());
            meta.setUpdateDate(new Date());
            return meta;
        }


    }

}
