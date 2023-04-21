package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.feed.EventDao;
import ru.yandex.practicum.filmorate.storage.friend.FriendDao;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userBdStorage;
    private final FriendDao friendDao;
    private final LikeDao likeDao;
    private final EventDao eventDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;
    private static final String ERROR_MESSAGE = "Передан несущетвующий id";


    public List<User> getUsers() {
        return userBdStorage.getUsers().stream()
                .map(user -> {
                    user.getFriends().addAll(friendDao.getFriends(user.getId()));
                    return user;
                })
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        return userBdStorage.create(user);
    }

    public User updateUser(User user) {
        return userBdStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        User user = userBdStorage.getUserById(id);
        user.getFriends().addAll(friendDao.getFriends(user.getId()));
        return user;
    }

    public void delete(Long id) {
        userBdStorage.delete(id);
    }

    public void addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friendUser = getUserById(friendId);

        friendDao.addFriend(user, friendUser);
        eventDao.addEvent(new Event(id, friendId, EventType.FRIEND, Operation.ADD));
        log.info("Для пользователя id={} добавлен друг id={}", id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friendUser = getUserById(friendId);

        friendDao.deleteFriend(user, friendUser);
        eventDao.addEvent(new Event(id, friendId, EventType.FRIEND, Operation.REMOVE));
        log.info("У пользователя id={} удален друг id={}", id, friendId);
    }

    public List<User> getFriends(Long id) {
        User user = getUserById(id);
        if (user != null) {
            Set<Long> friends = friendDao.getFriends(id);
            return friends.stream()
                    .map(friend -> getUserById(friend))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException(ERROR_MESSAGE);
        }
    }

    public List<User> getCommonFriends(Long id, Long userId) {
        User firstUser = getUserById(id);
        User secondUser = getUserById(userId);
        if (firstUser != null && secondUser != null) {
            Set<Long> firstUserFriends = firstUser.getFriends();
            Set<Long> secondUserFriends = secondUser.getFriends();
            Set<Long> commonFriends = firstUserFriends.stream()
                    .filter(secondUserFriends::contains)
                    .collect(Collectors.toSet());
            return userBdStorage.getUsersByIds(commonFriends).stream()
                    .map(user -> {
                        user.getFriends().addAll(friendDao.getFriends(user.getId()));
                        return user;
                    })
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException(ERROR_MESSAGE);
        }
    }

    public Set<Film> getRecommendedFilms(Long userId) {
        User user = getUserById(userId); //проверка существования пользователя, если его нет выкинет ошибку
        return likeDao.getRecommendedFilms(userId).stream()
                .map(film -> makeFilm(film))
                .collect(Collectors.toSet());
    }

    public List<Event> getFeed(long userId) {
        getUserById(userId);
        return eventDao.getFeed(userId);
    }

    private Film makeFilm(Film film) {
        Long id = film.getId();
        film.getLikes().addAll(likeDao.getFilmLikes(id));
        film.getGenres().addAll(genreDao.getFilmGenres(id));
        film.getDirectors().addAll(directorDao.getFilmDirector(id));
        return film;
    }
}
