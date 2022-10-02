package com.squareshift.shopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShippingConfig
{
    @Bean
    public Map<Config, BigDecimal> getShippingCost()
    {
        Map<Config, BigDecimal> shippingCostMap = new HashMap<>();
        Config config1 = new Config(1, 1); // distance: <5, weight: <2
        shippingCostMap.put(config1, new BigDecimal(12));
        config1 = new Config(1, 2); // distance: <5, weight: 2-5
        shippingCostMap.put(config1, new BigDecimal(14));
        config1 = new Config(1, 3); // distance: <5, weight: 5-20
        shippingCostMap.put(config1, new BigDecimal(16));
        config1 = new Config(1, 4); // distance: <5, weight: >20
        shippingCostMap.put(config1, new BigDecimal(21));

        Config config2 = new Config(2, 1); // distance: 5-20, weight: <2
        shippingCostMap.put(config2, new BigDecimal(15));
        config2 = new Config(2,2); // distance: 5-20, weight: 2-5
        shippingCostMap.put(config2, new BigDecimal(18));
        config2 = new Config(2, 3); // distance: 5-20, weight: 5-20
        shippingCostMap.put(config2, new BigDecimal(25));
        config2 = new Config(2, 4);// distance: 5-20, weight: > 20
        shippingCostMap.put(config2, new BigDecimal(35));

        Config config3 = new Config(3, 1); // distance: 20-50, weight: <2
        shippingCostMap.put(config3, new BigDecimal(20));
        config3 = new Config(3, 2); // distance: 20-50, weight: 2-5
        shippingCostMap.put(config3, new BigDecimal(24));
        config3 = new Config(3, 3); // distance: 20-50, weight: 5-20
        shippingCostMap.put(config3, new BigDecimal(30));
        config3 = new Config(3, 4); // distance: 20-50, weight: >20
        shippingCostMap.put(config3, new BigDecimal(50));

        Config config4 = new Config(4, 1); // distance: 50-500, weight: <2
        shippingCostMap.put(config4, new BigDecimal(50));
        config4 = new Config(4, 2); // distance: 50-500, weight: 2-5
        shippingCostMap.put(config4, new BigDecimal(55));
        config4 = new Config(4, 3); // distance: 50-500, weight: 5-20
        shippingCostMap.put(config4, new BigDecimal(80));
        config4 = new Config(4, 4); // distance: 50-500, weight: >20
        shippingCostMap.put(config4, new BigDecimal(90));

        Config config5 = new Config(5, 1); // distance: 500-800, weight: <2
        shippingCostMap.put(config5, new BigDecimal(100));
        config5 = new Config(5, 2); // distance: 500-800, weight: 2-5
        shippingCostMap.put(config5, new BigDecimal(110));
        config5 = new Config(5, 3); // distance: 500-800, weight: 5-20
        shippingCostMap.put(config5, new BigDecimal(130));
        config5 = new Config(5, 4); // distance: 500-800, weight: >20
        shippingCostMap.put(config5, new BigDecimal(150));

        Config config6 = new Config(6, 1); // distance: >800, weight: <2
        shippingCostMap.put(config6, new BigDecimal(220));
        config6 = new Config(6, 2); // distance: >800, weight: 2-5
        shippingCostMap.put(config6, new BigDecimal(250));
        config6 = new Config(6, 3); // distance: >800, weight: 5-20
        shippingCostMap.put(config6, new BigDecimal(270));
        config6 = new Config(6, 4); // distance: >800, weight: >20
        shippingCostMap.put(config6, new BigDecimal(300));

        return  shippingCostMap;
    }
}
