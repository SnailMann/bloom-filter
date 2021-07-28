package com.snailmann.bloom.utils;

/**
 * @author liwenjie
 */
public class BloomUtils {

    /**
     * Computes m (total bits of Bloom filter) which is expected to achieve, for the specified
     * expected insertions, the required false positive probability.
     *
     * <p>See http://en.wikipedia.org/wiki/Bloom_filter#Probability_of_false_positives for the
     * formula.
     *
     * @param n expected insertions (must be positive)
     * @param p false positive rate (must be 0 < p < 1)
     */
    public static int optimalNumOfBits(int n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    public static int resizeNumOfBits(int n, double b) {
        return (int) Math.min(n * b, Integer.MAX_VALUE);
    }

    // Cheat sheet:
    //
    // m: total bits
    // n: expected insertions
    // b: m/n, bits per insertion
    // p: expected false positive probability
    //
    // 1) Optimal k = b * ln2
    // 2) p = (1 - e ^ (-kn/m))^k
    // 3) For optimal k: p = 2 ^ (-k) ~= 0.6185^b
    // 4) For optimal k: m = -nlnp / ((ln2) ^ 2)

    /**
     * Computes the optimal k (number of hashes per element inserted in Bloom filter), given the
     * expected insertions and total number of bits in the Bloom filter.
     *
     * <p>See http://en.wikipedia.org/wiki/File:Bloom_filter_fp_probability.svg for the formula.
     *
     * @param n expected elements (must be positive)
     * @param m total number of bits in Bloom filter (must be positive)
     */
    public static int optimalNumOfHashFunctions(int n, int m) {
        // (m / n) * log(2), but avoid truncation due to division!
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    public static int optimalNumOfHashFunctions(int n, double p) {
        long m = optimalNumOfBits(n, p);
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /**
     * Computes the false positive probability
     * 1. p = (1 - e ^ (-kn/m))^k
     */
    public static double optimalFpp(int n, int m, int k) {
        int point = 100000;
        return (double) Math.round((point * Math.pow(1 - Math.exp((double) (-k * n) / m), k))) / point;
    }

    /**
     * Bits occupied per element
     *
     * @param n expected elements
     * @param m total number of bits in Bloom filter
     * @return b (space occupied by unit element)
     */
    public static double bitsOfItem(int n, int m) {
        return (double) m / n;
    }
}
