package com.interview.kafka.order.config;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;

public class LoggingProducerListener implements ProducerListener<Object, Object> {
    private static final Logger log = LoggerFactory.getLogger(LoggingProducerListener.class);

    @Override
    public void onSuccess(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata) {
        log.info("Order event published topic={} partition={} offset={} key={}",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), producerRecord.key());
    }

    @Override
    public void onError(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata,
                        Exception exception) {
        log.error("Failed to publish order event topic={} key={}",
                producerRecord.topic(), producerRecord.key(), exception);
    }
}
