package com.snailmann.bloom.filter.config;

import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
     * Encoding used for storing hash values as strings
     */
    static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Number of elements expected to be supported by the filter
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
     * Space occupied by unit element (unit: bit)
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
        configuration.setB(bitsOfElement(n, m));
        return configuration;
    }

    public static FilterConfiguration defaultConfiguration() {
        return config(10000, 0.00046);
    }

    public static FilterConfiguration copyOf(FilterConfiguration src) {
        FilterConfiguration target = new FilterConfiguration();
        target.setN(src.getN());
        target.setM(src.getM());
        target.setP(src.getP());
        target.setK(src.getK());
        target.setB(src.getB());
        return target;
    }

    public static Charset charset() {
        return CHARSET;
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
