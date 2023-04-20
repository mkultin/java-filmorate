package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.feed.EventDao;
import ru.yandex.practicum.filmorate.storage.feed.EventDaoImpl;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventDaoTests {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private EventDao eventDao;
    private Event event1;
    private Event event2;
    private Event event3;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addDefaultScripts()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        eventDao = new EventDaoImpl(jdbcTemplate);

        jdbcTemplate.update("insert into users (name, login, email, birthday)" +
                "values ('User1', 'user1', 'user1@userland.com', '2000-01-01')");

        event1 = Event.builder()
                .userId(1)
                .entityId(1)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .timestamp(Timestamp.valueOf("2023-01-01 12:00:00"))
                .build();

        event2 = Event.builder()
                .userId(1)
                .entityId(2)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Timestamp.valueOf("2023-01-01 12:30:00"))
                .build();

        event3 = Event.builder()
                .userId(1)
                .entityId(55)
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .timestamp(Timestamp.valueOf("2023-02-02 12:00:00"))
                .build();
    }

    @Test
    void getFeedShouldReturnUserFeed() {
        jdbcTemplate.update("insert into event (user_id, entity_id, event_type, operation, create_time) " +
                "values (1, 1, 'LIKE', 'ADD', '2023-01-01 12:00:00')");

        jdbcTemplate.update("insert into event (user_id, entity_id, event_type, operation, create_time) " +
                "values (1, 2, 'FRIEND', 'ADD', '2023-01-01 12:30:00')");

        jdbcTemplate.update("insert into event (user_id, entity_id, event_type, operation, create_time) " +
                "values (1, 55, 'REVIEW', 'UPDATE', '2023-02-02 12:00:00')");

        event1.setEventId(1);
        event2.setEventId(2);
        event3.setEventId(3);

        assertEquals(List.of(event1, event2, event3), eventDao.getFeed(1));
    }

    @Test
    void addEventShouldAddEventToDb() {
        eventDao.addEvent(event1);
        eventDao.addEvent(event2);
        eventDao.addEvent(event3);

        event1.setEventId(1);
        event2.setEventId(2);
        event3.setEventId(3);

        assertEquals(List.of(event1, event2, event3), eventDao.getFeed(1));
    }
}
