package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTests {

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private final UserService userService;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .email("first@user.ru")
                .login("first_user")
                .name("First Name")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();

        secondUser = User.builder()
                .email("second@user.ru")
                .login("second_user")
                .name("Second Name")
                .birthday(LocalDate.of(1998, 8, 8))
                .build();

        thirdUser = User.builder()
                .email("third@user.ru")
                .login("third_user")
                .name("Third Name")
                .birthday(LocalDate.of(1997, 7, 7))
                .build();
    }

    @Test
    public void shouldGetUserById() {
        userService.addUser(firstUser);
        User user = userService.getUserById(1L);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);

        userService.addUser(secondUser);
        user = userService.getUserById(2L);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(2);
    }

    @Test
    public void shouldGetUsers() {
        userService.addUser(firstUser);
        userService.addUser(secondUser);
        List<User> users = userService.getUsers();
        assertThat(users).contains(firstUser);
        assertThat(users).contains(secondUser);
    }

    @Test
    public void shouldUpdateUser() {
        userService.addUser(firstUser);
        User userToUpd = User.builder()
                .id(1L)
                .email("first@user.ru")
                .login("first_user")
                .name("First users Name")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
        userService.updateUser(userToUpd);

        User updUser = userService.getUserById(1L);

        assertThat(updUser).isNotNull();
        assertThat(updUser.getId()).isEqualTo(userToUpd.getId());
        assertThat(updUser.getName()).isEqualTo(userToUpd.getName());
    }

    @Test
    public void shouldAddGetAndDeleteFriends() {
        userService.addUser(firstUser);
        userService.addUser(secondUser);
        userService.addUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());

        List<User> friends = userService.getFriends(firstUser.getId());
        assertThat(friends.size()).isEqualTo(1);

        userService.addFriend(firstUser.getId(), thirdUser.getId());

        friends = userService.getFriends(firstUser.getId());
        assertThat(friends.size()).isEqualTo(2);

        userService.addFriend(secondUser.getId(), firstUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());

        List<User> commonFriends = userService.getCommonFriends(firstUser.getId(), secondUser.getId());
        assertThat(commonFriends.size()).isEqualTo(1);

        userService.deleteFriend(firstUser.getId(), thirdUser.getId());

        friends = userService.getFriends(firstUser.getId());
        assertThat(friends.size()).isEqualTo(1);
    }
}
