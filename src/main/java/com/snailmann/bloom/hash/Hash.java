package com.snailmann.bloom.hash;

import java.util.function.Supplier;

/**
 * @author liwenjie
 */
public abstract class Hash {

    /**
     * Initialize k hash functions
     *
     * @param k number of hash functions
     */
    abstract void hashes(int k);

    /**
     * "long & Long.MAX_VALUE" to get a positive number
     */
    public int index(Supplier<Long> hashSupplier, int len) {
        return (int) ((hashSupplier.get() & Long.MAX_VALUE) % len);
    }
}
