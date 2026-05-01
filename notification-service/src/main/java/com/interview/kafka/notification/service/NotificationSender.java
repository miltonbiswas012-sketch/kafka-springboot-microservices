package com.interview.kafka.notification.service;

import com.interview.kafka.common.event.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(NotificationSender.class);

    public void sendPaymentNotification(PaymentProcessedEvent event) {
        log.info("Notification sent customerId={} orderId={} paymentStatus={} amount={}",
                event.getCustomerId(), event.getOrderId(), event.getStatus(), event.getAmount());
    }
}
