package com.interview.kafka.order.service;

import com.interview.kafka.common.config.TopicNames;
import com.interview.kafka.common.dto.CreateOrderRequest;
import com.interview.kafka.common.dto.CreateOrderResponse;
import com.interview.kafka.common.event.EventMetadata;
import com.interview.kafka.common.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request, String correlationId) {
        String orderId = "ORD-" + UUID.randomUUID();
        String eventId = UUID.randomUUID().toString();
        String resolvedCorrelationId = StringUtils.hasText(correlationId) ? correlationId : eventId;

        EventMetadata metadata = new EventMetadata(
                eventId,
                resolvedCorrelationId,
                "ORDER_CREATED",
                "order-service",
                Instant.now()
        );

        OrderCreatedEvent event = new OrderCreatedEvent(
                metadata,
                orderId,
                request.getCustomerId(),
                request.getAmount(),
                request.getCurrency(),
                request.getItems()
        );

        ListenableFuture<SendResult<String, OrderCreatedEvent>> future =
                kafkaTemplate.send(TopicNames.ORDER_CREATED, orderId, event);

        future.addCallback(
                result -> { },
                ex -> {
                    throw new IllegalStateException("Kafka publish failed for order " + orderId, ex);
                }
        );

        return new CreateOrderResponse(orderId, "ORDER_ACCEPTED", "Order event accepted for async processing");
    }
}
