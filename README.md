# java-filmorate
Template repository for Filmorate project.

# Database diagram

![](database_diagram.png)

# Примеры запросов
## Получение всех фильмов с лайками

    SELECT f.film_id AS film_id,
           f.name AS film_name,
           COUNT(fl.user_id) AS film_likes
    FROM film AS f
    LEFT JOIN film_like AS fl
    GROUB BY film_id;

## Получение всех пользователей

    SELECT (*)
    FROM user;

