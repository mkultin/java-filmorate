package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    List<User> getUsers();

    User getUserById(Long id);

    List<User> getUsersByIds(Set<Long> ids);

    User create(User user);

    User updateUser(User user);

    void delete(Long id);
}
