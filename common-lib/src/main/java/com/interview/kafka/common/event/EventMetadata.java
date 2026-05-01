package com.interview.kafka.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class EventMetadata {
    private String eventId;
    private String correlationId;
    private String eventType;
    private String source;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant eventTime;

    public EventMetadata() {
    }

    public EventMetadata(String eventId, String correlationId, String eventType, String source, Instant eventTime) {
        this.eventId = eventId;
        this.correlationId = correlationId;
        this.eventType = eventType;
        this.source = source;
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }
}
