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


Отзыв напарника по пир-ревью:
Молодец, хорошая диаграмма! На мой взгляд, поставленная задача выполнена. Сделаю 2 небольших дополнения. Может лучше убрать знаки вопроса возле некоторых типов данных? Также, можно убрать скобки из второго запроса, когда выбираешь все значения, результат будет тот же. Удачи со следующими ТЗ!)
