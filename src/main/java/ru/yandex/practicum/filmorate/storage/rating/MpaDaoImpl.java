package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT * FROM rating";
        return jdbcTemplate.query(sql, this::makeRating);
    }

    @Override
    public Mpa findById(Integer mpaId) {
        if (mpaId == null) {
            throw new ValidationException("Передан пустой id");
        }
        String sqlQuery = "SELECT * FROM rating WHERE rating_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, mpaId);
        if (userRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeRating, mpaId);
        } else {
            throw new FilmNotFoundException("MPA не найден");
        }
    }

    private Mpa makeRating(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(
                resultSet.getInt("rating_id"),
                resultSet.getString("name")
        );
    }
}
