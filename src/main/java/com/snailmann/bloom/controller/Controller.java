package com.snailmann.bloom.controller;

import com.snailmann.bloom.filter.BloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liwenjie
 */
@RestController
@RequestMapping("/filter-server/v1")
public class Controller {

    @Autowired
    BloomFilter<Long> boomBloomFilter;

    @GetMapping
    public boolean contains(long id) {
        return boomBloomFilter.mightContains(id);
    }

    @GetMapping("/add")
    public void add(long id) {
        boomBloomFilter.put(id);
    }


}
