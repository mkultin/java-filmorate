package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTests {

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private final UserService userService;
    private final FilmService filmService;

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

    @Test
    public void shouldGetRecommendedFilms() {
        userService.addUser(firstUser);
        userService.addUser(secondUser);

        Set<Film> films = userService.getRecommendedFilms(firstUser.getId());
        assertThat(films.size()).isEqualTo(0);

        Film firstFilm = Film.builder()
                .name("The First Film")
                .description("Description of The First Film")
                .releaseDate(LocalDate.of(2000, 2, 4))
                .duration(120)
                .mpa(new Mpa(1, "Комедия"))
                .build();
        firstFilm.getGenres().add(new Genre(1, "G"));

        Film secondFilm = Film.builder()
                .name("The Second Film")
                .description("Description of The Second Film")
                .releaseDate(LocalDate.of(2004, 4, 8))
                .duration(140)
                .mpa(new Mpa(2, "Драма"))
                .build();
        secondFilm.getGenres().add(new Genre(2, "PG"));

        filmService.addFilm(firstFilm);
        filmService.addFilm(secondFilm);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.addLike(firstFilm.getId(), secondUser.getId());
        films = userService.getRecommendedFilms(firstUser.getId());
        assertThat(films.size()).isEqualTo(0);

        filmService.addLike(secondFilm.getId(), secondUser.getId());
        films = userService.getRecommendedFilms(firstUser.getId());
        assertThat(films.size()).isEqualTo(1);

        filmService.addLike(secondFilm.getId(), firstUser.getId());
        films = userService.getRecommendedFilms(firstUser.getId());
        assertThat(films.size()).isEqualTo(0);

        Film thirdFilm = Film.builder()
                .name("The Third Film")
                .description("Description of The Third Film")
                .releaseDate(LocalDate.of(2008, 6, 12))
                .duration(160)
                .mpa(new Mpa(3, "Мультфильм"))
                .build();
        thirdFilm.getGenres().add(new Genre(3, "PG-13"));
        filmService.addFilm(thirdFilm);

        filmService.addLike(thirdFilm.getId(), firstUser.getId());
        films = userService.getRecommendedFilms(firstUser.getId());
        assertThat(films.size()).isEqualTo(0);
    }
}
