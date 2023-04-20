package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

public interface EventDao {
    List<Event> getFeed(long userId);

    void addEvent(Event event);
}
