package com.interview.kafka.common.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderCreatedEvent {
    private EventMetadata metadata;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private List<OrderItem> items = new ArrayList<>();

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(EventMetadata metadata, String orderId, String customerId, BigDecimal amount,
                             String currency, List<OrderItem> items) {
        this.metadata = metadata;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.items = items;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
