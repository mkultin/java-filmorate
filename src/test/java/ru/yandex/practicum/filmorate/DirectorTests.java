package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorTests {

    private final DirectorDao directorDao;
    private Director firstDirector;
    private Director secondDirector;

    @BeforeEach
    void beforeEach() {
        firstDirector = new Director();
        firstDirector.setName("First Director");

        secondDirector = new Director();
        secondDirector.setName("Second Director");
    }

    @Test
    void shouldCRUDDirector() {
        directorDao.create(firstDirector);

        Director director = directorDao.findById(1);
        assertThat(director).isNotNull();
        assertThat(director.getId()).isEqualTo(1);
        assertThat(director.getName()).isEqualTo(firstDirector.getName());

        directorDao.create(secondDirector);

        List<Director> directors = directorDao.findAll();
        assertThat(directors).isNotNull();
        assertThat(directors.size()).isEqualTo(2);
        assertThat(directors).contains(director);

        Director newDirector = new Director(2, "The Second Director");
        directorDao.update(newDirector);

        director = directorDao.findById(newDirector.getId());
        assertThat(director).isNotNull();
        assertThat(director.getName()).isEqualTo(newDirector.getName());

        directorDao.deleteDirector(firstDirector.getId());

        directors = directorDao.findAll();
        assertThat(directors).isNotNull();
        assertThat(directors.size()).isEqualTo(1);
        assertThat(directors).contains(director);
    }


}
