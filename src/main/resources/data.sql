MERGE INTO rating(rating_id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genre (genre_id, name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');
           
MERGE INTO event_type (type_id, name)
    VALUES (1, 'LIKE'),
           (2, 'REVIEW'),
           (3, 'FRIEND');
           
MERGE INTO operation (operation_id, name)
    VALUES (1, 'REMOVE'),
           (2, 'ADD'),
           (3, 'UPDATE');
           
          
insert INTO USERS (email, LOGIN, NAME, BIRTHDAY)
VALUES ('aa@aa.aa', 'aaa', 'A', '2000-01-01');

insert INTO USERS (email, LOGIN, NAME, BIRTHDAY)
VALUES ('aa@aa.aa', 'bbb', 'B', '2000-01-01');

insert INTO USERS (email, LOGIN, NAME, BIRTHDAY)
VALUES ('aa@aa.aa', 'ccc', 'C', '2000-01-01');

insert INTO USERS (email, LOGIN, NAME, BIRTHDAY)
VALUES ('aa@aa.aa', 'ddd', 'D', '2000-01-01');

INSERT INTO USER_FRIEND(USER_ID, FRIEND_ID) values(1, 2);

INSERT INTO USER_FRIEND(USER_ID, FRIEND_ID) values(1, 4);

INSERT INTO USER_FRIEND(USER_ID, FRIEND_ID) values(3, 2);

INSERT INTO USER_FRIEND(USER_ID, FRIEND_ID) values(3, 1);

INSERT INTO EVENT (USER_ID, ENTITY_ID, TYPE_ID, OPERATION_ID, EVENT_TIMESTAMP)
values(1, 22, 1, 2, '2023-02-01 18:00:00');

INSERT INTO EVENT (USER_ID, ENTITY_ID, TYPE_ID, OPERATION_ID, EVENT_TIMESTAMP)
values(2, 22, 2, 2, '2023-02-01 18:30:00');

INSERT INTO EVENT (USER_ID, ENTITY_ID, TYPE_ID, OPERATION_ID, EVENT_TIMESTAMP)
values(4, 22, 3, 3, '2023-02-01 18:40:00');

INSERT INTO EVENT (USER_ID, ENTITY_ID, TYPE_ID, OPERATION_ID, EVENT_TIMESTAMP)
values(2, 22, 2, 1, '2023-02-01 18:55:00');

INSERT INTO EVENT (USER_ID, ENTITY_ID, TYPE_ID, OPERATION_ID, EVENT_TIMESTAMP)
values(4, 22, 1, 3, '2023-02-01 19:00:00');

INSERT INTO EVENT (USER_ID, ENTITY_ID, TYPE_ID, OPERATION_ID, EVENT_TIMESTAMP)
values(2, 22, 1, 2, '2023-02-01 20:00:00');
