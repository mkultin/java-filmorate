package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.EventDao;
import ru.yandex.practicum.filmorate.storage.friend.FriendDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userBdStorage;
    private final FriendDao friendDao;
    private final LikeDao likeDao;
    private final EventDao eventDao;
    private static final String ERROR_MESSAGE = "Передан несущетвующий id";

    @Autowired
    public UserService(@Qualifier("userBdStorage") UserStorage userBdStorage, FriendDao friendDao,
                       LikeDao likeDao, EventDao eventDao) {
        this.userBdStorage = userBdStorage;
        this.friendDao = friendDao;
        this.likeDao = likeDao;
        this.eventDao = eventDao;
    }

    public List<User> getUsers() {
        return userBdStorage.getUsers();
    }

    public User addUser(User user) {
        return userBdStorage.create(user);
    }

    public User updateUser(User user) {
        return userBdStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userBdStorage.getUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        User user = userBdStorage.getUserById(id);
        User friendUser = userBdStorage.getUserById(friendId);

        friendDao.addFriend(user, friendUser);
        eventDao.addEvent(new Event(id, friendId, "FRIEND", "ADD"));
        log.info("Для пользователя id={} добавлен друг id={}", id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userBdStorage.getUserById(id);
        User friendUser = userBdStorage.getUserById(friendId);

        friendDao.deleteFriend(user, friendUser);
        eventDao.addEvent(new Event(id, friendId, "FRIEND", "DELETE"));
        log.info("У пользователя id={} удален друг id={}", id, friendId);
    }

    public List<User> getFriends(Long id) {
        User user = userBdStorage.getUserById(id);
        if (user != null) {
            Set<Long> friends = friendDao.getFriends(id);
            return friends.stream()
                    .map(userBdStorage::getUserById)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException(ERROR_MESSAGE);
        }
    }

    public List<User> getCommonFriends(Long id, Long userId) {
        User firstUser = userBdStorage.getUserById(id);
        User secondUser = userBdStorage.getUserById(userId);
        if (firstUser != null && secondUser != null) {
            Set<Long> firstUserFriends = firstUser.getFriends();
            Set<Long> secondUserFriends = secondUser.getFriends();
            Set<Long> commonFriends = firstUserFriends.stream()
                    .filter(secondUserFriends::contains)
                    .collect(Collectors.toSet());
            return userBdStorage.getUsersByIds(commonFriends);
        } else {
            throw new NotFoundException(ERROR_MESSAGE);
        }
    }

    public Set<Film> getRecommendedFilms(Long userId) {
        User user = userBdStorage.getUserById(userId); //проверка существования пользователя, если его нет выкинет ошибку
        return likeDao.getRecommendedFilms(userId);
    }

    public List<Event> getFeed(long userId) {
        userBdStorage.getUserById(userId);
        return eventDao.getFeed(userId);
    }
}
