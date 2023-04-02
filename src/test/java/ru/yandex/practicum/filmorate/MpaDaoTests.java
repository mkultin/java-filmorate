package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDaoTests {

    private final MpaDao mpaDao;

    @Test
    void shouldGetRatingsAndGetRatingById() {
        List<Mpa> ratings = mpaDao.findAll();
        Mpa mpaG = mpaDao.findById(1);
        Mpa mpaR = mpaDao.findById(4);

        assertThat(ratings).isNotNull();
        assertThat(mpaG).isNotNull();
        assertThat(mpaR).isNotNull();
        assertThat(mpaG.getName()).isEqualTo("G");
        assertThat(mpaR.getName()).isEqualTo("R");
        assertThat(ratings.size()).isEqualTo(5);
        assertThat(ratings).contains(mpaG);
        assertThat(ratings).contains(mpaR);
    }
}
