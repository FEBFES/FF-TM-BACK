package com.febfes.fftmback.config.cache;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class HazelcastConfiguration {

    @Value("${cache.users.ttl:600}")
    private int usersCacheTtl;

    private static final List<Class<?>> KRYO_TYPES = List.of(
            UserDto.class,
            ProjectDto.class,
            ProjectForUserDto.class,
            ProjectEntity.class,
            RoleName.class,
            RoleEntity.class
    );

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

        // === serializer ===
        SerializationConfig serializationConfig = config.getSerializationConfig();
        int typeId = 1000;
        for (Class<?> clazz : KRYO_TYPES) {
            serializationConfig.addSerializerConfig(
                    new SerializerConfig()
                            .setTypeClass(clazz)
                            .setImplementation(new KryoSerializer<>(clazz, typeId++))
            );
        }

        // === metrics.management-center.enabled = true ===
        MetricsConfig metricsConfig = config.getMetricsConfig();
        metricsConfig.getManagementCenterConfig().setEnabled(true);
        metricsConfig.setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);
    }
}
