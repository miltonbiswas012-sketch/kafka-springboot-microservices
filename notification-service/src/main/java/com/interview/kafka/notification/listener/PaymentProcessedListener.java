package com.interview.kafka.notification.listener;

import com.interview.kafka.common.config.TopicNames;
import com.interview.kafka.common.event.PaymentProcessedEvent;
import com.interview.kafka.notification.service.NotificationSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessedListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessedListener.class);

    private final NotificationSender notificationSender;

    public PaymentProcessedListener(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics = TopicNames.PAYMENT_PROCESSED,
            groupId = "${app.kafka.consumer-group}",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void onPaymentProcessed(PaymentProcessedEvent event,
                                   ConsumerRecord<String, PaymentProcessedEvent> record,
                                   Acknowledgment acknowledgment) {
        log.info("Notification service received orderId={} topic={} partition={} offset={}",
                event.getOrderId(), record.topic(), record.partition(), record.offset());

        notificationSender.sendPaymentNotification(event);
        acknowledgment.acknowledge();
    }

    @DltHandler
    public void handlePaymentProcessedDlt(PaymentProcessedEvent event,
                                          ConsumerRecord<String, PaymentProcessedEvent> record,
                                          Acknowledgment acknowledgment) {
        log.error("Payment event moved to DLT orderId={} topic={} partition={} offset={}",
                event.getOrderId(), record.topic(), record.partition(), record.offset());
        acknowledgment.acknowledge();
    }
}
