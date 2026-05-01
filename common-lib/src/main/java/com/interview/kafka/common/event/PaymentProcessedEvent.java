package com.interview.kafka.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentProcessedEvent {
    private EventMetadata metadata;
    private String orderId;
    private String customerId;
    private String paymentId;
    private String status;
    private BigDecimal amount;
    private String failureReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant processedAt;

    public PaymentProcessedEvent() {
    }

    public PaymentProcessedEvent(EventMetadata metadata, String orderId, String customerId, String paymentId,
                                 String status, BigDecimal amount, String failureReason, Instant processedAt) {
        this.metadata = metadata;
        this.orderId = orderId;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
    }

    public EventMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(EventMetadata metadata) {
        this.metadata = metadata;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
