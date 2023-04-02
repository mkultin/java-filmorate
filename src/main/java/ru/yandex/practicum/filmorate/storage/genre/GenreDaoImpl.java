package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    @Override
    public Genre findById(Integer genreId) {
        if (genreId == null) {
            throw new ValidationException("Передан пустой id");
        }
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, genreId);
        if (genreRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, genreId);
        } else {
            throw new FilmNotFoundException("Жанр не найден");
        }
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT * FROM genre AS g WHERE g.genre_id IN (" +
                "SELECT fg.genre_id FROM film_genre AS fg WHERE fg.film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sql, this::makeGenre, filmId));
    }

    @Override
    public void setFilmGenre(Long filmId, Integer genreId) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public void deleteFilmGenres(Long filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getInt("genre_id"),
                resultSet.getString("name")
        );
    }
}
