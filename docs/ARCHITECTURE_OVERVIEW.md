# Architecture Overview

This project is a Kafka-based Spring Boot microservices system. It simulates an ecommerce order flow where one service accepts an order, another service processes payment, and another service sends a notification.

## High-Level Flow

```text
Customer creates order
        |
        v
order-service
        |
        | publishes OrderCreatedEvent
        v
Kafka topic: orders.created.v1
        |
        v
payment-service
        |
        | publishes PaymentProcessedEvent
        v
Kafka topic: payments.processed.v1
        |
        v
notification-service
        |
        v
Notification sent/logged
```

## Project Modules

```text
kafka
├── common-lib
├── order-service
├── payment-service
└── notification-service
```

## common-lib

`common-lib` contains shared classes used by all services.

Important classes:

```text
CreateOrderRequest
CreateOrderResponse
OrderCreatedEvent
PaymentProcessedEvent
EventMetadata
TopicNames
```

This avoids duplicating event and DTO classes across services.

## order-service

`order-service` is the producer service.

It exposes this REST API:

```text
POST http://localhost:8081/api/v1/orders
```

When this API is called, it creates an `OrderCreatedEvent` and publishes it to Kafka topic:

```text
orders.created.v1
```

Important classes:

```text
OrderServiceApplication
OrderController
OrderService
KafkaTopicConfig
KafkaProducerConfig
LoggingProducerListener
```

Responsibilities:

- Accept order request.
- Validate request using `@Valid`.
- Generate `orderId`.
- Generate event metadata.
- Publish order event to Kafka.

## payment-service

`payment-service` is both a Kafka consumer and producer.

It consumes from:

```text
orders.created.v1
```

It processes the order payment. If the amount is less than or equal to `50000`, payment is marked successful. If the amount is greater than `50000`, payment is marked for manual review.

Then it publishes `PaymentProcessedEvent` to:

```text
payments.processed.v1
```

Important classes:

```text
PaymentServiceApplication
OrderCreatedListener
PaymentProcessor
KafkaConsumerConfig
```

Responsibilities:

- Listen to order-created events.
- Process payment logic.
- Publish payment-processed events.
- Retry failed messages.
- Send failed messages to DLT after retries.

## notification-service

`notification-service` is a Kafka consumer service.

It consumes from:

```text
payments.processed.v1
```

When it receives a payment event, it sends/logs a notification.

Important classes:

```text
NotificationServiceApplication
PaymentProcessedListener
NotificationSender
KafkaConsumerConfig
```

Responsibilities:

- Listen to payment-processed events.
- Send customer notification.
- Retry failed messages.
- Send failed messages to DLT after retries.

## End-To-End Execution

1. User sends order request to `order-service`.
2. `order-service` validates the request using `@Valid`.
3. `order-service` generates `orderId`, `eventId`, `correlationId`, and `eventTime`.
4. `order-service` publishes `OrderCreatedEvent` to Kafka topic `orders.created.v1`.
5. `payment-service` consumes the order event using `@KafkaListener`.
6. `payment-service` processes payment.
7. `payment-service` publishes `PaymentProcessedEvent` to Kafka topic `payments.processed.v1`.
8. `notification-service` consumes the payment event.
9. `notification-service` sends/logs notification.

## Why Kafka Is Used

Kafka decouples services.

`order-service` does not directly call `payment-service`. It only publishes an event. This means `payment-service` can be down temporarily and the order event can still remain in Kafka until `payment-service` comes back.

This is a common real-time microservices pattern.

## Kafka Concepts Used

### Producers

```text
order-service
payment-service
```

### Consumers

```text
payment-service
notification-service
```

### Topics

```text
orders.created.v1
payments.processed.v1
```

### Consumer Groups

```text
payment-service-v1
notification-service-v1
```

### Message Key

```text
orderId
```

Using `orderId` as the Kafka message key keeps events for the same order in the same Kafka partition. This preserves ordering for that specific order.

## Production Features

The project includes practical production-style Kafka features:

```text
acks: all
enable.idempotence: true
manual acknowledgement
consumer groups
retry topic
dead-letter topic
ErrorHandlingDeserializer
Kafka topic creation
correlation ID
event metadata
actuator health endpoints
Prometheus metrics endpoint
Docker Compose
Kafka UI
```

## Retry And DLT Flow

Both consumer services use:

```java
@RetryableTopic
@DltHandler
```

If message processing fails, the flow is:

```text
Kafka message
   -> retry 1
   -> retry 2
   -> retry 3
   -> DLT topic
```

DLT means Dead Letter Topic. It stores failed messages after retries are exhausted.

This is important in real projects because one bad message should not block the entire consumer.

## Interview Explanation

You can explain the project like this:

```text
This project uses event-driven microservices with Kafka. The order service accepts an order request and publishes an order-created event. Payment service consumes that event, processes payment, and publishes a payment-processed event. Notification service consumes the payment event and sends a notification. Services are loosely coupled, scalable independently, and failures are handled using retries and DLT.
```

## Main URLs

```text
Order API:
http://localhost:8081/api/v1/orders

Kafka UI:
http://localhost:8080

Order Health:
http://localhost:8081/actuator/health

Payment Health:
http://localhost:8082/actuator/health

Notification Health:
http://localhost:8083/actuator/health
```
