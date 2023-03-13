package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        User user;
        if (users.containsKey(id)) {
            user = users.get(id);
        } else {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден.");
        }
        return user;
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        List<User> usersByIds = new ArrayList<>();
        for (Long friendId : ids) {
            usersByIds.add(users.get(friendId));
        }
        return usersByIds;
    }

    @Override
    public User create(User user) {
        validateName(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь {}, id={}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            validateName(user);
            users.put(user.getId(), user);
            log.info("Пользователь id={} обновлен: {}", user.getId(), user.getName());
        } else {
            throw new UserNotFoundException("Пользователь id=" + user.getId() + "не найден.");
        }
        return user;
    }

    @Override
    public void delete(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("Пользователь id={} удален", id);
        } else {
            throw new ValidationException("Пользователь id=" + id + "не найден.");
        }
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
    }
}
