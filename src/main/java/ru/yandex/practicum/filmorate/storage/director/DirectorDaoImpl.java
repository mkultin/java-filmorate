package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM director";
        log.info("Получены все режиссеры.");
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    @Override
    public Director findById(Integer id) {
        String sql = "SELECT * FROM director WHERE director_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (sqlRowSet.next()) {
            log.info("Получен режиссер id = {}.", id);
            return jdbcTemplate.queryForObject(sql, this::makeDirector, id);
        } else {
            throw new NotFoundException("Режиссер не найден");
        }
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        Integer id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
        director.setId(id);
        log.info("Добавлен новый режиссер {}, id = {}", director.getName(), director.getId());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE director SET name = ? WHERE director_id = ?";
        int queryResult = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        validateQueryResult(queryResult);
        log.info("Обновлен режиссер {}, id = {}", director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Integer id) {
        String sqlQuery = "DELETE FROM director WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Удален режиссер id = {}", id);
    }

    @Override
    public Set<Director> getFilmDirector(Long filmId) {
        String sqlQuery = "SELECT * FROM director " +
                "WHERE director_id IN (SELECT director_id FROM film_director WHERE film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::makeDirector, filmId));
    }

    @Override
    public void deleteFilmDirectors(Long filmId) {
        String sqlQuery = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        log.info("Удален список режиссеров для фильма id = {}", filmId);
    }

    @Override
    public void updateFilmDirector(Film film) {
        Long filmId = film.getId();
        List<Object[]> batch = new ArrayList<>();
        for (Director director : film.getDirectors()) {
            Object[] values = new Object[]{filmId, director.getId()};
            batch.add(values);
        }
        String sqlQuery = "INSERT INTO film_director (film_id, director_id) " +
                "VALUES(?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, batch);
        log.info("Обновлен список режиссеров для фильма id = {}", filmId);
    }

    private Director makeDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(
                resultSet.getInt("director_id"),
                resultSet.getString("name")
        );
    }

    private void validateQueryResult(int queryResult) {
        if (queryResult == 0) {
            throw new NotFoundException("Режиссер не найден.");
        }
    }
}
