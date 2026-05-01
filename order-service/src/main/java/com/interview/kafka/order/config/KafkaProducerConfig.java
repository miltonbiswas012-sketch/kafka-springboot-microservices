package com.interview.kafka.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.ProducerListener;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerListener<Object, Object> producerListener() {
        return new LoggingProducerListener();
    }
}
