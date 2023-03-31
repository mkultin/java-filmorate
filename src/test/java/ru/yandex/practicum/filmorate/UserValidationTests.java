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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserValidationTests {

    private User firstUser;
    private User secondUser;
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
    }

    @Test
    public void createAndGetUsers() {
        userService.addUser(firstUser);
        User user = userService.getUserById(1L);

        assertEquals(1, user.getId());

        userService.addUser(secondUser);
        user = userService.getUserById(2L);

        assertEquals(2, user.getId());
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

        assertEquals(userToUpd.getId(), updUser.getId());
        assertEquals(userToUpd.getName(), updUser.getName());
    }
}
