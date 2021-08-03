package com.snailmann.bloom.filter.config;

import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.snailmann.bloom.utils.BloomUtils.*;

/**
 * Bloom filter configuration
 *
 * @author liwenjie
 */
@Data
public class FilterConfig {

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
     * Bits occupied by each element (unit: bit)
     */
    private double c;

    /**
     * Fpp (false positive probability)
     */
    private double p;

    private Long createDate = System.currentTimeMillis();
    private Long modifyDate = this.createDate;

    public static FilterConfig config(int n, int k, double c) {
        FilterConfig configuration = new FilterConfig();
        configuration.setN(n);
        configuration.setK(k);
        configuration.setC(c);
        configuration.setM(resizeNumOfBits(n, c));
        configuration.setP(optimalFpp(n, configuration.getM(), k));
        return configuration;
    }

    public static FilterConfig config(int n, double p) {
        FilterConfig configuration = new FilterConfig();
        configuration.setN(n);
        configuration.setP(p);
        int m = optimalNumOfBits(n, p);
        configuration.setM(m);
        configuration.setK(optimalNumOfHashFunctions(n, m));
        configuration.setC(bitsOfElement(n, m));
        return configuration;
    }

    public static FilterConfig defaultConfig() {
        return config(10000, 0.00046);
    }

    public static FilterConfig copyOf(FilterConfig source) {
        FilterConfig target = new FilterConfig();
        target.setN(source.getN());
        target.setM(source.getM());
        target.setP(source.getP());
        target.setK(source.getK());
        target.setC(source.getC());
        return target;
    }

    public static Charset charset() {
        return CHARSET;
    }
}
