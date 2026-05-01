package com.interview.kafka.payment.service;

import com.interview.kafka.common.event.EventMetadata;
import com.interview.kafka.common.event.OrderCreatedEvent;
import com.interview.kafka.common.event.PaymentProcessedEvent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentProcessor {
    private static final BigDecimal MANUAL_REVIEW_LIMIT = new BigDecimal("50000.00");

    public PaymentProcessedEvent process(OrderCreatedEvent orderEvent) {
        boolean approved = orderEvent.getAmount().compareTo(MANUAL_REVIEW_LIMIT) <= 0;
        String status = approved ? "PAYMENT_SUCCESS" : "PAYMENT_REQUIRES_REVIEW";

        EventMetadata metadata = new EventMetadata(
                UUID.randomUUID().toString(),
                orderEvent.getMetadata().getCorrelationId(),
                "PAYMENT_PROCESSED",
                "payment-service",
                Instant.now()
        );

        return new PaymentProcessedEvent(
                metadata,
                orderEvent.getOrderId(),
                orderEvent.getCustomerId(),
                "PAY-" + UUID.randomUUID(),
                status,
                orderEvent.getAmount(),
                approved ? null : "Amount exceeds automatic approval limit",
                Instant.now()
        );
    }
}
