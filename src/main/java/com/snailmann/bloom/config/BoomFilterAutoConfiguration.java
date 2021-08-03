package com.snailmann.bloom.config;

import com.snailmann.bloom.filter.config.FilterConfig;
import com.snailmann.bloom.filter.BloomFilter;
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
    @ConditionalOnMissingBean(FilterConfig.class)
    public FilterConfig filterConfiguration() {
        return FilterConfig.defaultConfig();
    }

    @Bean
    @ConditionalOnMissingBean(BloomFilter.class)
    public BloomFilter simpleBoomFilter(FilterConfig configuration) {
        return BloomFilter.create(null, configuration);
    }
}
