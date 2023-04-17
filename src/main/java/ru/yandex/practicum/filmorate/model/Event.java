package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Timestamp timestamp;
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
        timestamp = Timestamp.from(Instant.now());
    }
}
