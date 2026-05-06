# Kafka With Spring Boot Microservices

This is an interview-ready production-style project showing Kafka with Spring Boot microservices.

## Architecture

```text
Client
  -> order-service REST API
  -> Kafka topic: orders.created.v1
  -> payment-service consumer
  -> Kafka topic: payments.processed.v1
  -> notification-service consumer
```

## Modules

- `common-lib`: shared DTOs, event contracts, topic constants.
- `order-service`: REST producer that publishes `OrderCreatedEvent`.
- `payment-service`: Kafka consumer that processes orders and publishes `PaymentProcessedEvent`.
- `notification-service`: Kafka consumer that receives payment events and sends a notification.

## Important Production Patterns Included

- `@SpringBootApplication`, `@EnableKafka`, `@Configuration`, `@Bean`, `@RestController`, `@Service`, `@Component`
- `@KafkaListener` consumers
- `@RetryableTopic` retry topic handling
- `@DltHandler` dead-letter topic handling
- Manual acknowledgement with `Acknowledgment`
- JSON serialization and deserialization with trusted packages
- `ErrorHandlingDeserializer`
- Idempotent producer settings
- Consumer groups
- Topic creation using `TopicBuilder`
- Actuator and Prometheus metrics endpoints
- Docker Compose with Kafka, Zookeeper, Kafka UI, and all services

## Run Locally With Maven

Start Kafka first:

```powershell
cd C:\Users\bshov\OneDrive\kafka
docker compose up -d zookeeper kafka kafka-ui
```

Run each service in a separate terminal:

```powershell
mvn -pl order-service -am spring-boot:run
mvn -pl payment-service -am spring-boot:run
mvn -pl notification-service -am spring-boot:run
```

Kafka UI:

```text
http://localhost:8080
```

## Run Everything With Docker

```powershell
cd C:\Users\bshov\OneDrive\kafka
docker compose up --build
```

## Test Request

```powershell
curl -X POST http://localhost:8081/api/v1/orders `
  -H "Content-Type: application/json" `
  -H "X-Correlation-Id: interview-demo-001" `
  -d "{\"customerId\":\"CUST-1001\",\"amount\":2499.00,\"currency\":\"INR\",\"items\":[{\"sku\":\"PHONE-CASE\",\"quantity\":1,\"unitPrice\":2499.00}]}"
```

Expected flow:

1. `order-service` returns `202 Accepted`.
2. `orders.created.v1` receives the event.
3. `payment-service` consumes the event and publishes to `payments.processed.v1`.
4. `notification-service` consumes the payment event and logs the notification.

## Health URLs

- Order service: `http://localhost:8081/actuator/health`
- Payment service: `http://localhost:8082/actuator/health`
- Notification service: `http://localhost:8083/actuator/health`

## Useful Interview Explanation

This system is asynchronous. The order API does not wait for payment or notification. It accepts the order, publishes a Kafka event, and lets independent services react. This gives loose coupling, better scalability, replay ability, and fault isolation.

Use the order ID as the Kafka key. That keeps all events for the same order on the same partition, preserving order for that aggregate.

## Detailed Run And Test Guide

For step-by-step IntelliJ run instructions, API testing commands, Kafka UI verification, and common fixes, read:

```text
RUN_AND_TEST_GUIDE.md
```
