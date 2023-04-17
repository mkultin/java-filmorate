package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

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
        String sqlQuery = "SELECT e.event_timestamp, e.user_id, e.event_id, " +
                "e.entity_id, et.name AS event_type, o.name AS operation " +
                "FROM event e " +
                "JOIN event_type et ON e.type_id = et.type_id " +
                "JOIN operation o ON e.operation_id = o.operation_id " +
                "WHERE user_id = ?" +
                "ORDER BY e.event_timestamp";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public void addEvent(Event event) {
        String sqlQuery = "INSERT INTO event (user_id, entity_id, type_id, operation_id, event_timestamp) " +
                "VALUES (?, ?, (SELECT type_id FROM event_type WHERE name = ?), " +
                "(SELECT operation_id FROM operation WHERE name = ?), ?)";

        jdbcTemplate.update(sqlQuery,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType(),
                event.getOperation(),
                event.getTimestamp());
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .timestamp(rs.getTimestamp("event_timestamp"))
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
