package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Event {
    private LocalDateTime timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long eventId;
    private long entityId;

    public Event(long userId, long entityId, String eventType, String operation) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
        timestamp = LocalDateTime.now();
    }
}
