package com.febfes.fftmback.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Slf4j
@ImportAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
public class TestTets {

    @Autowired
    private ListableBeanFactory beanFactory;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void printAllProxies() {
        for (String name : beanFactory.getBeanDefinitionNames()) {
            Object bean = beanFactory.getBean(name);
            if (AopUtils.isAopProxy(bean)) {
                System.out.println("Proxy bean: " + name + " (" + bean.getClass() + ")");
            }
        }
    }
}
