package com.snailmann.bloom.hash;

import com.facebook.util.digest.MurmurHash;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liwenjie
 */
public class Murmur3Hash extends Hash {

    private final int seed = 2;

    private List<MurmurHash> hashes = List.of(new MurmurHash((seed << 1) - 1),
            new MurmurHash((seed << 2) - 1));

    @Override
    public void createHashes(int k) {
        List<MurmurHash> hashes = new ArrayList<>(k);
        for (int i = 1; i <= k; i++) {
            // seed = 3,7,15,31,63...
            hashes.add(new MurmurHash((seed << i) - 1));
        }
        this.hashes = hashes;
    }

    @Override
    public int[] hashes(byte[] bytes, int len) {
        int[] indexs = new int[hashes.size()];
        for (int i = 0; i < hashes.size(); i++) {
            var hash = hashes.get(i);
            indexs[i] = index(() -> hash.hash(hash.hashToLong(bytes)), len);
        }
        return indexs;
    }

}
