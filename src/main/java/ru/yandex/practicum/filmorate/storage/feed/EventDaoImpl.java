package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class EventDaoImpl implements EventDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getFeed(long userId) {
        String sqlQuery = "SELECT create_time, user_id, event_id, " +
                "entity_id, event_type, operation " +
                "FROM event " +
                "WHERE user_id = ?" +
                "ORDER BY create_time";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public void addEvent(@Valid Event event) {
        String sqlQuery = "INSERT INTO event (user_id, entity_id, event_type, operation, create_time) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp());
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .timestamp(rs.getTimestamp("create_time"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
