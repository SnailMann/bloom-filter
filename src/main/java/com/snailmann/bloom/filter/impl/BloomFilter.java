package com.snailmann.bloom.filter.impl;

import com.snailmann.bloom.filter.BaseBloomFilter;
import com.snailmann.bloom.filter.config.FilterConfiguration;
import com.snailmann.bloom.hash.Murmur3Hash;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static com.snailmann.bloom.filter.config.FilterConfiguration.charset;

/**
 * @author liwenjie
 */
@Slf4j
public final class BloomFilter<E> extends BaseBloomFilter<E> {

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
    private final Murmur3Hash murmur3 = new Murmur3Hash();

    private BloomFilter() {
        this(null, FilterConfiguration.defaultConfiguration());
    }

    private BloomFilter(String tag, FilterConfiguration configuration) {
        super(tag, configuration);
        bitCount = new LongAdder();
        currentSize = new LongAdder();
        // hashes
        murmur3.hashes(configuration.getK());
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
    public void put(byte[] bs) {
        int m = configuration.getM();
        for (var h : murmur3.hashes) {
            int index = murmur3.index(() -> h.hash(h.hashToLong(bs)), m);
            byte a = this.bytes[index / B];
            byte b = (byte) (1 << (index % B));
            this.bytes[index / B] = (byte) (a | b);
            // bit count
            if ((a & b) == 0) {
                this.bitCount.increment();
            }
        }
        // modify date
        this.currentSize.increment();
        this.configuration.getMeta().setUpdateDate(new Date());
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
    public boolean mightContains(byte[] bs) {
        int m = configuration.getM();
        for (var h : murmur3.hashes) {
            int index = murmur3.index(() -> h.hash(h.hashToLong(bs)), m);
            byte a = bytes[index / B];
            byte b = (byte) (1 << (index % B));
            if ((a & b) == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "BoomFilter{" +
                "bytes_len=" + bytes.length + "," +
                "tag=" + getTag() + "," +
                "bit_count=" + getBitCount() + "，" +
                "element_size=" + getCurrentSize() + "，" +
                "config=" + configuration +
                '}';
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

    public static <R> BloomFilter<R> create(String tag, FilterConfiguration configuration) {
        return new BloomFilter<>(tag, configuration);
    }

    public static <R> BloomFilter<R> create(String tag, int n, double p) {
        return new BloomFilter<>(tag, FilterConfiguration.config(n, p));
    }

    public static <R> BloomFilter<R> create(String tag, int n, int k, double b) {
        return new BloomFilter<>(tag, FilterConfiguration.config(n, k, b));
    }
}
