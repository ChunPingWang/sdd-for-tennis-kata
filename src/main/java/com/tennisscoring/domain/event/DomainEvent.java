package com.tennisscoring.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for all domain events.
 * Provides common properties like event ID and timestamp.
 */
public abstract class DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredAt;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }
    
    /**
     * Get the unique identifier of this event.
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }
    
    /**
     * Get the timestamp when this event occurred.
     * @return the occurrence timestamp
     */
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    /**
     * Get the type of this event.
     * @return the event type name
     */
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public String toString() {
        return getEventType() + "{" +
                "eventId='" + eventId + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}