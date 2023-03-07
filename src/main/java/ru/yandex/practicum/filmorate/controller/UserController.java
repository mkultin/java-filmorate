package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @GetMapping
    public List<User> getFilms() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateName(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь {}, id={}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            validateName(user);
            users.put(user.getId(), user);
            log.info("Пользователь id={} обновлен: {}", user.getId(), user.getName());
        } else {
            throw new ValidationException("Пользователь с таким id не найден.");
        }
        return user;
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
    }
}
