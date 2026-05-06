# Run And Test Guide

This guide explains how to run and test the Kafka Spring Boot microservices project in IntelliJ IDEA.

## Project Location

```text
C:\Users\bshov\OneDrive\kafka
```

## Prerequisites

Install or confirm these are available:

- Java 11
- Maven
- Docker Desktop
- IntelliJ IDEA

Check from PowerShell:

```powershell
java -version
mvn -version
docker version
```

## Open Project In IntelliJ

1. Open IntelliJ IDEA.
2. Click `File > Open`.
3. Select:

```text
C:\Users\bshov\OneDrive\kafka
```

4. Open the root `kafka` folder, not only one service folder.
5. Wait for Maven import to complete.
6. Open the Maven panel and click `Reload All Maven Projects`.

You should see these modules:

```text
common-lib
order-service
payment-service
notification-service
```

## Set Java Version

In IntelliJ:

```text
File > Project Structure > Project
```

Set:

```text
SDK: Java 11
Language level: 11
```

## Build The Project

From IntelliJ Terminal or PowerShell:

```powershell
cd C:\Users\bshov\OneDrive\kafka
mvn clean package -DskipTests
```

Expected result:

```text
BUILD SUCCESS
```

## Start Kafka

From IntelliJ Terminal or PowerShell:

```powershell
cd C:\Users\bshov\OneDrive\kafka
docker compose up -d zookeeper kafka kafka-ui
```

If Docker says the container name already exists, run:

```powershell
docker compose down
docker rm -f interview-kafka interview-zookeeper interview-kafka-ui
docker compose up -d zookeeper kafka kafka-ui
```

Check containers:

```powershell
docker ps
```

Kafka UI:

```text
http://localhost:8080
```

## Run Services In IntelliJ

Run these main classes one by one:

### 1. Order Service

```text
com.interview.kafka.order.OrderServiceApplication
```

Runs on:

```text
http://localhost:8081
```

### 2. Payment Service

```text
com.interview.kafka.payment.PaymentServiceApplication
```

Runs on:

```text
http://localhost:8082
```

### 3. Notification Service

```text
com.interview.kafka.notification.NotificationServiceApplication
```

Runs on:

```text
http://localhost:8083
```

## Check Service Health

Open these URLs in browser:

```text
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

## Test Happy Path Order

Run this from PowerShell:

```powershell
curl -X POST http://localhost:8081/api/v1/orders `
  -H "Content-Type: application/json" `
  -H "X-Correlation-Id: interview-demo-001" `
  -d "{\"customerId\":\"CUST-1001\",\"amount\":2499.00,\"currency\":\"INR\",\"items\":[{\"sku\":\"PHONE-CASE\",\"quantity\":1,\"unitPrice\":2499.00}]}"
```

Expected API response:

```json
{
  "orderId": "ORD-...",
  "status": "ORDER_ACCEPTED",
  "message": "Order event accepted for async processing"
}
```

## Expected End-To-End Flow

```text
Client
  -> order-service REST API
  -> Kafka topic orders.created.v1
  -> payment-service consumes order event
  -> Kafka topic payments.processed.v1
  -> notification-service consumes payment event
```

## Verify Logs

In `order-service` logs, check:

```text
Order event published topic=orders.created.v1
```

In `payment-service` logs, check:

```text
Payment service received orderId=...
Payment event published orderId=... status=PAYMENT_SUCCESS
```

In `notification-service` logs, check:

```text
Notification service received orderId=...
Notification sent customerId=... orderId=... paymentStatus=PAYMENT_SUCCESS
```

## Verify In Kafka UI

Open:

```text
http://localhost:8080
```

Go to `Topics` and check:

```text
orders.created.v1
payments.processed.v1
```

You should see messages in both topics.

## Test Manual Review Case

Use a high amount:

```powershell
curl -X POST http://localhost:8081/api/v1/orders `
  -H "Content-Type: application/json" `
  -H "X-Correlation-Id: interview-demo-002" `
  -d "{\"customerId\":\"CUST-2002\",\"amount\":75000.00,\"currency\":\"INR\",\"items\":[{\"sku\":\"LAPTOP\",\"quantity\":1,\"unitPrice\":75000.00}]}"
```

Expected payment status in logs:

```text
PAYMENT_REQUIRES_REVIEW
```

## Test Validation Failure

Send invalid request without `customerId`:

```powershell
curl -X POST http://localhost:8081/api/v1/orders `
  -H "Content-Type: application/json" `
  -d "{\"amount\":2499.00,\"currency\":\"INR\",\"items\":[{\"sku\":\"PHONE-CASE\",\"quantity\":1,\"unitPrice\":2499.00}]}"
```

Expected result:

```text
HTTP 400 Bad Request
```

## Stop Everything

Stop Spring Boot services from IntelliJ using the red stop button.

Stop Kafka:

```powershell
cd C:\Users\bshov\OneDrive\kafka
docker compose down
```

## Common Problems

### common-lib package not found

Open the root folder:

```text
C:\Users\bshov\OneDrive\kafka
```

Do not open only:

```text
C:\Users\bshov\OneDrive\kafka\order-service
```

Then reload Maven.

### Port already in use

Check what is running:

```powershell
netstat -ano | findstr :8081
netstat -ano | findstr :8082
netstat -ano | findstr :8083
```

Stop the old process or change the service port in `application.yml`.

### Kafka container conflict

Run:

```powershell
docker compose down
docker rm -f interview-kafka interview-zookeeper interview-kafka-ui
docker compose up -d zookeeper kafka kafka-ui
```

### Kafka UI does not open

Check containers:

```powershell
docker ps
```

Then restart:

```powershell
docker compose restart kafka-ui
```
