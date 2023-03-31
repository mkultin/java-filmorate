package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendDaoImpl implements FriendDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "INSERT INTO user_friend (user_id, friend_id, status) " +
                "VALUES (?, ?, ?)";
        if (friend.getFriends().contains(user)) {
            jdbcTemplate.update(sqlQuery, user.getId(), friend.getId(), true);
            updateFriendStatus(user, friend, true);
        } else {
            jdbcTemplate.update(sqlQuery, user.getId(), friend.getId(), false);
        }
    }

    @Override
    public void deleteFriend(User user, User friend) {
        if (friend.getFriends().contains(user)) {
            updateFriendStatus(friend, user, false);
        }
        String sqlQuery = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public Set<Long> getFriends(Long id) {
        String sqlQuery = "SELECT user_id FROM users WHERE user_id IN (" +
                "SELECT friend_id FROM user_friend WHERE user_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                (resultSet, rowNum) -> resultSet.getLong("user_id"), id));
    }

    @Override
    public List<User> getCommonFriends(Long id, Long userId) {
        return null;
    }

    private void updateFriendStatus(User user, User friend, Boolean status) {
        String sqlQuery = "UPDATE user_friend SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, status, friend.getId(), user.getId());
    }
}
