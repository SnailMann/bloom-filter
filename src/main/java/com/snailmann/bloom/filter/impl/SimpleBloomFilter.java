package com.snailmann.bloom.filter.impl;

import com.snailmann.bloom.filter.BaseBloomFilter;
import com.snailmann.bloom.filter.FilterConfiguration;
import com.snailmann.bloom.hash.Hash;
import com.snailmann.bloom.hash.Murmur3Hash;
import com.snailmann.bloom.utils.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author liwenjie
 */
@Slf4j
public final class SimpleBloomFilter extends BaseBloomFilter<String> {

    /**
     * Data field of bloom filter
     */
    private byte[] bytes;

    /**
     * Current number of bits 1
     */
    private LongAdder bitCount;

    /**
     * Current number of items
     */
    private LongAdder currentSize;

    /**
     * Murmur3 hash functions
     */
    private final Murmur3Hash murmur3 = new Murmur3Hash();

    private SimpleBloomFilter() {
        this(null, FilterConfiguration.defaultConfiguration());
    }

    public SimpleBloomFilter(String tag, FilterConfiguration configuration) {
        super(tag, configuration);
        // adder
        bitCount = new LongAdder();
        currentSize = new LongAdder();
        // hashes
        murmur3.hashes(configuration.getK());
        // fill bytes
        this.bytes = new byte[(int) Math.ceil((double) configuration.getM() / B)];
    }

    /**
     * Put a item to bloom filter
     *
     * @param obj item
     */
    @Override
    public void put(String obj) {
        byte[] bs = obj.getBytes();
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
        // update date
        this.currentSize.increment();
        this.configuration.getMeta().setUpdateDate(new Date());
    }

    /**
     * Put a batch of items to bloom filter
     *
     * @param objs items
     */
    @Override
    public void putAll(List<String> objs) {
        for (String o : objs) {
            try {
                put(o);
            } catch (Exception e) {
                log.error("put item [{}] error", o);
            }
        }
    }

    /**
     * Item may have appeared in bloom filter. The probability of fpp will misjudge the non-existent item
     *
     * @param obj item
     * @return whether item exists
     */
    @Override
    public boolean mightContains(String obj) {
        byte[] bs = obj.getBytes();
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
        return "SimpleBoomFilter{" +
                "bytes_len=" + bytes.length + "," +
                "tag=" + getTag() + "," +
                "bit_count=" + getBitCount() + "，" +
                "item_size=" + getCurrentSize() + "，" +
                "config=" + configuration +
                '}';
    }

    public int getCurrentSize() {
        return this.currentSize.intValue();
    }

    public long getBitCount() {
        return bitCount.longValue();
    }

    public static SimpleBloomFilter create() {
        return new SimpleBloomFilter();
    }

    public static SimpleBloomFilter create(String tag, FilterConfiguration configuration) {
        return new SimpleBloomFilter(tag, configuration);
    }

    public static SimpleBloomFilter create(String tag, int n, double p) {
        return new SimpleBloomFilter(tag, FilterConfiguration.config(n, p));
    }

    public static SimpleBloomFilter create(String tag, int n, int k, double b) {
        return new SimpleBloomFilter(tag, FilterConfiguration.config(n, k, b));
    }

    public static void main(String[] args) {

        SimpleBloomFilter boomFilter = SimpleBloomFilter.create("test", 333, 0.0003d);
        System.out.println(boomFilter.toString());
        System.out.println(ByteUtils.bitCount(boomFilter.bytes));

        for (int i = 0; i < 300; i++) {
            boomFilter.put(i + "");
        }

        System.out.println(boomFilter.mightContains("123"));
        System.out.println(boomFilter.mightContains("12"));
        System.out.println(boomFilter.mightContains("333"));

        System.out.println(boomFilter.toString());
        System.out.println(ByteUtils.bitCount(boomFilter.bytes));

    }
}
