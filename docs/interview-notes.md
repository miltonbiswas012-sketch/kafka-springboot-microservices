# Kafka Spring Boot Interview Notes

## What Is Kafka?

Apache Kafka is a distributed event streaming platform. It stores records in topics, splits topics into partitions, and lets producers write events while consumers read them independently.

## Core Terms

- Topic: logical stream of events, for example `orders.created.v1`.
- Partition: ordered log inside a topic.
- Offset: position of a message inside a partition.
- Producer: application that writes messages.
- Consumer: application that reads messages.
- Consumer group: group of consumers sharing topic partitions.
- Broker: Kafka server.
- Key: determines the target partition.
- Retention: how long Kafka keeps records.
- DLT: dead-letter topic for messages that cannot be processed successfully.

## Why Kafka In Microservices?

- Services are loosely coupled.
- Producers do not need to know consumers.
- Consumers can be added without changing producers.
- Events can be replayed.
- Each service can scale independently.
- Kafka can buffer traffic during downstream failures.

## Producer Best Practices

- Use a meaningful key, such as `orderId`, to preserve order per aggregate.
- Use `acks=all` for durability.
- Use `enable.idempotence=true` to prevent duplicate writes caused by retries.
- Use compression for better throughput.
- Use callbacks or listeners to track failed sends.
- Include `eventId`, `correlationId`, `eventType`, `source`, and `eventTime`.

## Consumer Best Practices

- Disable auto commit for important processing.
- Acknowledge only after business logic succeeds.
- Use consumer groups for horizontal scaling.
- Handle poison messages with retry and DLT.
- Keep processing idempotent because Kafka gives at-least-once delivery by default.
- Monitor consumer lag.

## Common Interview Questions

### How Does Kafka Maintain Ordering?

Kafka maintains ordering only inside a partition. If all events for an order use `orderId` as the key, those events go to the same partition and stay ordered.

### What Happens If A Consumer Fails?

If the consumer does not commit the offset, another consumer in the same group can reprocess the message after rebalance. This is why consumer logic must be idempotent.

### What Is At-Least-Once Delivery?

Kafka may deliver the same message more than once, but it should not lose committed messages. The consumer must handle duplicate delivery safely.

### How Do You Handle Failed Messages?

Use retries for temporary issues. If retries are exhausted, route the message to a DLT. Teams then inspect, fix, and replay the DLT message if needed.

### Why Use Correlation ID?

Correlation ID connects logs and events across services. In this project, the REST request correlation ID goes into Kafka event metadata and is passed downstream.

### Difference Between Retry Topic And Blocking Retry?

Blocking retry sleeps in the consumer thread and can block partition progress. Retry topics move the message to delayed retry topics, freeing the main consumer to continue processing other messages.

### What Is Consumer Lag?

Consumer lag is the difference between the latest produced offset and the last consumed offset. High lag means consumers are behind producers.

## Annotations Used In This Project

- `@SpringBootApplication`: starts the Spring Boot service.
- `@EnableKafka`: enables Kafka listener infrastructure.
- `@EnableKafkaRetryTopic`: enables non-blocking retry topics.
- `@Configuration`: marks configuration classes.
- `@Bean`: registers Kafka topics, factories, and listeners.
- `@RestController`: exposes REST APIs.
- `@RequestMapping`, `@PostMapping`: maps HTTP endpoints.
- `@RequestBody`, `@RequestHeader`: reads request data.
- `@Valid`: validates request DTOs.
- `@Service`: business logic component.
- `@Component`: generic Spring component.
- `@KafkaListener`: consumes Kafka topic records.
- `@RetryableTopic`: creates retry flow for failed records.
- `@DltHandler`: handles records after retries are exhausted.

## Real-Time Project Checklist

- Schema strategy: JSON for simple systems, Avro/Protobuf with schema registry for larger organizations.
- Security: SASL_SSL or mTLS in production.
- Observability: logs, metrics, consumer lag alerts, tracing.
- DLT operations: dashboard and replay process.
- Idempotency: store processed event IDs or use natural idempotent business keys.
- Versioning: topic names or schema fields should support evolution.
- Capacity: choose partition count based on expected parallelism.
