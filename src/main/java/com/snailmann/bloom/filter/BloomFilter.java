package com.snailmann.bloom.filter;

import com.snailmann.bloom.filter.basic.BaseFilter;
import com.snailmann.bloom.filter.config.FilterConfig;
import com.snailmann.bloom.hash.Hash;
import com.snailmann.bloom.hash.Murmur3Hash;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static com.snailmann.bloom.filter.config.FilterConfig.charset;

/**
 * @author liwenjie
 */
@Slf4j
public final class BloomFilter<E> extends BaseFilter<E> {

    /**
     * Data field of bloom filter
     */
    private byte[] bytes;

    /**
     * Current number of bits 1
     */
    private LongAdder bitCount;

    /**
     * Current number of elements
     */
    private LongAdder currentSize;

    /**
     * Murmur3 hash functions
     */
    private final Hash murmur3 = new Murmur3Hash();

    private BloomFilter() {
        this(null, FilterConfig.defaultConfig());
    }

    private BloomFilter(String name, FilterConfig configuration) {
        super(name, configuration);
        bitCount = new LongAdder();
        currentSize = new LongAdder();
        // hashes
        murmur3.createHashes(configuration.getK());
        // fill bytes
        this.bytes = new byte[(int) Math.ceil((double) configuration.getM() / B)];
    }

    /**
     * Put a element to bloom filter
     *
     * @param element element
     */
    @Override
    public void put(E element) {
        byte[] bs = element.toString().getBytes(charset());
        put(bs);
    }

    /**
     * Put a raw element to bloom filter
     *
     * @param bs bytes of element
     */
    @Override
    public synchronized void put(byte[] bs) {
        int m = config().getM();
        int[] indexs = murmur3.hashes(bs, m);
        for (int index : indexs) {
            byte bits = this.bytes[index / B];
            byte t = (byte) (1 << (B_MASK - (index % B)));
            this.bytes[index / B] = (byte) (bits | t);
            // bit count
            if ((bits & t) == 0) {
                this.bitCount.increment();
            }
        }
        // modify date
        this.currentSize.increment();
        this.config().setCreateDate(System.currentTimeMillis());
    }

    /**
     * Put a batch of elements to bloom filter
     *
     * @param elements elements
     */
    @Override
    public void putAll(List<E> elements) {
        for (E o : elements) {
            try {
                put(o);
            } catch (Exception e) {
                log.error("put element [{}] error", o, e);
            }
        }
    }

    /**
     * Element may have appeared in bloom filter. The probability of fpp will misjudge the non-existent element
     *
     * @param element element
     * @return whether element exists
     */
    @Override
    public boolean mightContains(E element) {
        byte[] bs = element.toString().getBytes(charset());
        return mightContains(bs);
    }

    @Override
    public synchronized boolean mightContains(byte[] bs) {
        int m = config().getM();
        int[] indexs = murmur3.hashes(bs, m);
        for (int index : indexs) {
            byte bits = bytes[index / B];
            byte t = (byte) (1 << (B_MASK - (index % B)));
            if ((bits & t) == 0) {
                return false;
            }
        }
        return true;
    }

    public int getCurrentSize() {
        return this.currentSize.intValue();
    }

    public long getBitCount() {
        return bitCount.longValue();
    }

    public static <R> BloomFilter<R> create() {
        return new BloomFilter<>();
    }

    public static <R> BloomFilter<R> create(String name, FilterConfig configuration) {
        return new BloomFilter<>(name, configuration);
    }

    public static <R> BloomFilter<R> create(String name, int n, double p) {
        return new BloomFilter<>(name, FilterConfig.config(n, p));
    }

    public static <R> BloomFilter<R> create(String name, int n, int k, double b) {
        return new BloomFilter<>(name, FilterConfig.config(n, k, b));
    }
}
