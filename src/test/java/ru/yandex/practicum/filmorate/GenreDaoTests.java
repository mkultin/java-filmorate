package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.user.genre.GenreDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTests {

    private final GenreDao genreDao;

    @Test
    void shouldGetGenresAndGetGenresById() {
        List<Genre> genres = genreDao.findAll();
        Genre comedy = genreDao.findById(1);
        Genre drama = genreDao.findById(2);

        assertThat(genres).isNotNull();
        assertThat(comedy).isNotNull();
        assertThat(drama).isNotNull();
        assertThat(comedy.getName()).isEqualTo("Комедия");
        assertThat(drama.getName()).isEqualTo("Драма");
        assertThat(genres.size()).isEqualTo(6);
        assertThat(genres).contains(comedy);
        assertThat(genres).contains(drama);
    }
}
