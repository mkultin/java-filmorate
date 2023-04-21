package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.debug("GET /users : get list of all users");
        return userService.getUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("POST /users : create user - {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("PUT /users : update user - {}", user);
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.debug("GET /users/{} : get user by ID", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.debug("DELETE /users/{} : delete user by ID", id);
        userService.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("PUT /users/{}/friends/{} : user add friend", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("DELETE /users/{}/friends/{} : delete friend", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.debug("GET /users/{}/friends : get list of user's friends", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.debug("GET /users/{}/friends/common/{} : get list of common friends", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendedFilms(@PathVariable Long id) {
        log.debug("GET /users/{}/recommendations : get list of films recommended by user", id);
        return userService.getRecommendedFilms(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Long id) {
        log.debug("GET /users/{}/feed : get list of feeds of user", id);
        return userService.getFeed(id);
    }
}
