package com.snailmann.bloom.config;

import com.snailmann.bloom.filter.FilterConfiguration;
import com.snailmann.bloom.filter.impl.SimpleBloomFilter;
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
    @ConditionalOnMissingBean(SimpleBloomFilter.class)
    public SimpleBloomFilter simpleBoomFilter(FilterConfiguration configuration) {
        return SimpleBloomFilter.create(null, configuration);
    }
}
