package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private static final String ERROR_MESSAGE = "Передан несущетвующий id";

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friendUser = userStorage.getUserById(friendId);
        if (user != null && friendUser != null) {
            user.getFriends().add(friendId);
            friendUser.getFriends().add(id);
            log.info("Для пользователя id={} добавлен друг id={}", id, friendId);
        } else {
            throw new UserNotFoundException(ERROR_MESSAGE);
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friendUser = userStorage.getUserById(friendId);
        if (user != null && friendUser != null) {
            user.getFriends().remove(friendId);
            friendUser.getFriends().remove(id);
            log.info("У пользователя id={} удален друг id={}", id, friendId);
        } else {
            throw new UserNotFoundException(ERROR_MESSAGE);
        }
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUserById(id);
        if (user != null) {
            Set<Long> friends = user.getFriends();
            return userStorage.getUsersByIds(friends);
        } else {
            throw new UserNotFoundException(ERROR_MESSAGE);
        }
    }

    public List<User> getCommonFriends(Long id, Long userId) {
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(userId);
        if (firstUser != null && secondUser != null) {
            Set<Long> firstUserFriends = firstUser.getFriends();
            Set<Long> secondUserFriends = secondUser.getFriends();
            Set<Long> commonFriends = firstUserFriends.stream()
                    .filter(secondUserFriends::contains)
                    .collect(Collectors.toSet());
            return userStorage.getUsersByIds(commonFriends);
        } else {
            throw new UserNotFoundException(ERROR_MESSAGE);
        }
    }
}
