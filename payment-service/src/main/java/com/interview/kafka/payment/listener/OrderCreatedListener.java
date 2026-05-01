package com.interview.kafka.payment.listener;

import com.interview.kafka.common.config.TopicNames;
import com.interview.kafka.common.event.OrderCreatedEvent;
import com.interview.kafka.common.event.PaymentProcessedEvent;
import com.interview.kafka.payment.service.PaymentProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final PaymentProcessor paymentProcessor;
    private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

    public OrderCreatedListener(PaymentProcessor paymentProcessor,
                                KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate) {
        this.paymentProcessor = paymentProcessor;
        this.kafkaTemplate = kafkaTemplate;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics = TopicNames.ORDER_CREATED,
            groupId = "${app.kafka.consumer-group}",
            containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event,
                               ConsumerRecord<String, OrderCreatedEvent> record,
                               Acknowledgment acknowledgment) {
        log.info("Payment service received orderId={} topic={} partition={} offset={}",
                event.getOrderId(), record.topic(), record.partition(), record.offset());

        PaymentProcessedEvent paymentEvent = paymentProcessor.process(event);
        kafkaTemplate.send(TopicNames.PAYMENT_PROCESSED, paymentEvent.getOrderId(), paymentEvent);

        acknowledgment.acknowledge();
        log.info("Payment event published orderId={} status={}", paymentEvent.getOrderId(), paymentEvent.getStatus());
    }

    @DltHandler
    public void handleOrderCreatedDlt(OrderCreatedEvent event,
                                      ConsumerRecord<String, OrderCreatedEvent> record,
                                      Acknowledgment acknowledgment) {
        log.error("Order event moved to DLT orderId={} topic={} partition={} offset={}",
                event.getOrderId(), record.topic(), record.partition(), record.offset());
        acknowledgment.acknowledge();
    }
}
