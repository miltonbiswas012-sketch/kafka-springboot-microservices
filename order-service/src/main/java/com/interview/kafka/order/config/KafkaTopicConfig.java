package com.interview.kafka.order.config;

import com.interview.kafka.common.config.TopicNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(TopicNames.ORDER_CREATED)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name(TopicNames.PAYMENT_PROCESSED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
