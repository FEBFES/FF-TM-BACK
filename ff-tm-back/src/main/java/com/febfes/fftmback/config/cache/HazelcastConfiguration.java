package com.febfes.fftmback.config.cache;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfiguration {

    @Value("${cache.users.ttl:600}")
    private int usersCacheTtl;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("dev");

        // === TTL config for "users" cache ===
        MapConfig usersCacheConfig = new MapConfig("users");
        usersCacheConfig.setTimeToLiveSeconds(usersCacheTtl);
        config.addMapConfig(usersCacheConfig);

        // === network.join.multicast.enabled = false ===
        NetworkConfig networkConfig = config.getNetworkConfig();
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);

        // === global-serializer ===
        SerializationConfig serializationConfig = config.getSerializationConfig();
        GlobalSerializerConfig globalSerializerConfig = new GlobalSerializerConfig();
        globalSerializerConfig.setClassName("com.febfes.fftmback.config.cache.KryoGlobalSerializer");
        globalSerializerConfig.setOverrideJavaSerialization(true);
        serializationConfig.setGlobalSerializerConfig(globalSerializerConfig);

        // === metrics.management-center.enabled = true ===
        MetricsConfig metricsConfig = config.getMetricsConfig();
        metricsConfig.getManagementCenterConfig().setEnabled(true);
        metricsConfig.setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);
    }
}
