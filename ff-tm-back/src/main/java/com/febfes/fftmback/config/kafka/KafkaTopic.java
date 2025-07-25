package com.febfes.fftmback.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!test")
public class KafkaTopic {

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notification-topic").build();
    }
}
