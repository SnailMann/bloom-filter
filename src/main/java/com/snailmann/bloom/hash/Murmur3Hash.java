package com.snailmann.bloom.hash;

import com.facebook.util.digest.MurmurHash;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liwenjie
 */
public class Murmur3Hash extends Hash {

    private final int seed = 2;

    public List<MurmurHash> hashes = List.of(new MurmurHash(seed >> 1), new MurmurHash(seed >> 2));

    @Override
    public void hashes(int k) {
        List<MurmurHash> hashes = new ArrayList<>(k);
        for (int i = 1; i <= k; i++) {
            // seed = 3,7,15,31,63...
            hashes.add(new MurmurHash((seed << i) - 1));
        }
        this.hashes = hashes;
    }

}
