package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendDao {

    void addFriend(User user, User friend);

    void deleteFriend(User user, User Friend);

    Set<Long> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long userId);
}
