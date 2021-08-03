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
    public abstract void createHashes(int k);

    /**
     * @param bytes bytes want to hash
     * @param len   m
     * @return indexs
     */
    public abstract int[] hashes(byte[] bytes, int len);

    /**
     * "long & Long.MAX_VALUE" to get a positive number
     */
    int index(Supplier<Long> hashSupplier, int len) {
        return (int) ((hashSupplier.get() & Long.MAX_VALUE) % len);
    }
}
