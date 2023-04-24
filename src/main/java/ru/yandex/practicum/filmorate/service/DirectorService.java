package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public List<Director> getDirectors() {
        return directorDao.findAll();
    }

    public Director getDirectorById(Integer id) {
        return directorDao.findById(id);
    }

    public Director addDirector(Director director) {
        return directorDao.create(director);
    }

    public Director updateDirector(Director director) {
        return directorDao.update(director);
    }

    public void deleteDirector(Integer id) {
        directorDao.deleteDirector(id);
    }
}
