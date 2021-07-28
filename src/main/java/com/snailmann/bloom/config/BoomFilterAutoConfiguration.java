package com.snailmann.bloom.config;

import com.snailmann.bloom.filter.config.FilterConfiguration;
import com.snailmann.bloom.filter.impl.BloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liwenjie
 */
@Slf4j
@Configuration
public class BoomFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FilterConfiguration.class)
    public FilterConfiguration filterConfiguration() {
        return FilterConfiguration.defaultConfiguration();
    }

    @Bean
    @ConditionalOnMissingBean(BloomFilter.class)
    public BloomFilter simpleBoomFilter(FilterConfiguration configuration) {
        return BloomFilter.create(null, configuration);
    }
}
