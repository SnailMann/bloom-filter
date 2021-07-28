package com.snailmann.bloom.controller;

import com.snailmann.bloom.filter.impl.BloomFilter;
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
    BloomFilter boomFilter;

    @GetMapping
    public boolean contains(long id) {
        return boomFilter.mightContains(id + "");
    }

    @GetMapping("/add")
    public void add(long id) {
        boomFilter.put(id + "");
    }


}
