package com.snailmann.bloom.filter;

import com.snailmann.bloom.filter.basic.BaseFilter;
import com.snailmann.bloom.filter.config.FilterConfig;
import com.snailmann.bloom.hash.Murmur3Hash;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static com.snailmann.bloom.filter.config.FilterConfig.charset;

/**
 * @author liwenjie
 */
@Slf4j
public final class Filter<E> extends BaseFilter<E> {

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

    private Filter() {
        this(null, FilterConfig.defaultConfig());
    }

    private Filter(String tag, FilterConfig configuration) {
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
        int m = config().getM();
        for (var hash : murmur3.hashes) {
            int index = murmur3.index(() -> hash.hash(hash.hashToLong(bs)), m);
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
        this.config().getMeta().setUpdateDate(new Date());
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
        int m = config().getM();
        for (var hash : murmur3.hashes) {
            int index = murmur3.index(() -> hash.hash(hash.hashToLong(bs)), m);
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

    public static <R> Filter<R> create() {
        return new Filter<>();
    }

    public static <R> Filter<R> create(String tag, FilterConfig configuration) {
        return new Filter<>(tag, configuration);
    }

    public static <R> Filter<R> create(String tag, int n, double p) {
        return new Filter<>(tag, FilterConfig.config(n, p));
    }

    public static <R> Filter<R> create(String tag, int n, int k, double b) {
        return new Filter<>(tag, FilterConfig.config(n, k, b));
    }

    public static void main(String[] args) {
        Long v = 15L;
        System.out.println(Long.bitCount(v));
    }
}
