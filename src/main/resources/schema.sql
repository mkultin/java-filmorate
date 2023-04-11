DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS user_friend CASCADE;
DROP TABLE IF EXISTS film_like CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS rating CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS film_director CASCADE;
DROP TABLE IF EXISTS director CASCADE;

CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS rating (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS film (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL,
    description varchar(200),
    release_date date,
    duration INTEGER,
    rating_id INTEGER REFERENCES rating(rating_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER REFERENCES film(film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre(genre_id) ON DELETE RESTRICT,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(50) NOT NULL,
    login varchar(50) NOT NULL,
    name varchar(50),
    birthday date
);

CREATE TABLE IF NOT EXISTS user_friend (
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    status boolean,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_like (
    film_id INTEGER REFERENCES film(film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS director (
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id INTEGER REFERENCES film(film_id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES director(director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

