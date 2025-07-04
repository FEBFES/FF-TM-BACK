package com.fftmback.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fftmback.authentication.config.RedisConfig;
import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.dto.RefreshTokenDto;
import com.fftmback.authentication.repository.RefreshTokenRepository;
import com.fftmback.authentication.service.RefreshTokenCacheService;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class RefreshTokenCacheServiceTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2.5")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    static {
        redisContainer.start();
    }

    @DynamicPropertySource
    static void addProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getFirstMappedPort());
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RefreshTokenCacheService refreshTokenCacheService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper redisObjectMapper;

    private static final String TEST_TOKEN = "test-token";
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setup() {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token(TEST_TOKEN)
                .userEntity(UserEntity.builder().id(TEST_USER_ID).build())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        refreshTokenRepository.save(entity);

        Objects.requireNonNull(cacheManager.getCache(RedisConfig.REFRESH_TOKENS_CACHE_NAME)).clear();
        Objects.requireNonNull(cacheManager.getCache(RedisConfig.REFRESH_TOKENS_BY_USER_CACHE_NAME)).clear();
    }

    @AfterEach
    void teardown() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    void testGetByTokenStoresToCache() {
        assertNull(Objects.requireNonNull(cacheManager.getCache(RedisConfig.REFRESH_TOKENS_CACHE_NAME))
                        .get(TEST_TOKEN),
                "Cache should be empty before call");

        refreshTokenCacheService.getByToken(TEST_TOKEN);

        Cache.ValueWrapper cached = Objects.requireNonNull(cacheManager.getCache(RedisConfig.REFRESH_TOKENS_CACHE_NAME))
                .get(TEST_TOKEN);
        assertNotNull(cached, "Cache should contain value after call");
        val value = redisObjectMapper.convertValue(cached.get(), RefreshTokenDto.class);
        assertInstanceOf(RefreshTokenDto.class, value);
    }

    @Test
    void testGetByUserIdStoresToCache() {
        assertNull(Objects.requireNonNull(cacheManager.getCache(RedisConfig.REFRESH_TOKENS_BY_USER_CACHE_NAME))
                .get(TEST_USER_ID));

        refreshTokenCacheService.getByUserId(TEST_USER_ID);

        Cache.ValueWrapper cached = Objects.requireNonNull(cacheManager.getCache(RedisConfig.REFRESH_TOKENS_BY_USER_CACHE_NAME))
                .get(TEST_USER_ID);
        assertNotNull(cached);
        val value = redisObjectMapper.convertValue(cached.get(), RefreshTokenDto.class);
        assertInstanceOf(RefreshTokenDto.class, value);
    }
}

