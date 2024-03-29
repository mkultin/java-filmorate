package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Component
@Qualifier("userBdStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
            log.info("Найден пользователь id = {}", id);
            return user;
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        if (ids != null) {
            return ids.stream()
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new ValidationException("Передан пустой user");
        }
        validateName(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(userId);
        log.info("Создан новый пользователь {}, id={}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new ValidationException("Передан пустой user");
        }
        validateName(user);
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
                "WHERE user_id = ?";
        int queryResult = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        validateQueryResult(queryResult);
        log.info("Пользователь id={} обновлен: {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        int queryResult = jdbcTemplate.update(sqlQuery, id);
        validateQueryResult(queryResult);
        log.info("Пользователь id = {} удален", id);
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        return user;
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
    }

    private void validateQueryResult(int queryResult) {
        if (queryResult == 0) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
