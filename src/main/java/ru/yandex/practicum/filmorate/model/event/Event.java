package ru.yandex.practicum.filmorate.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class Event {
    @PastOrPresent(message = "Некорректное время события")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Timestamp timestamp;
    @Positive(message = "Некорректный ID пользователя")
    private long userId;
    @NotBlank(message = "Не указан тип события")
    private EventType eventType;
    @NotBlank(message = "Не указан тип операции")
    private Operation operation;
    private long eventId;
    @Positive(message = "Некорректный ID сущности")
    private long entityId;

    public Event(long userId, long entityId, EventType eventType, Operation operation) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
        timestamp = Timestamp.from(Instant.now());
    }
}
