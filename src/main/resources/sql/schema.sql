CREATE TYPE PlayerPositionEnum AS ENUM (
  'GK',
  'DEF',
  'MIDF',
  'STR'
);
CREATE TYPE ContinentEnum AS ENUM (
    'EUROPA',
    'AFRICA',
    'AMERICA',
    'ASIA'
);



create table Player (
    id  SERIAL primary key,
    name varchar(255),
    age int,
    position PlayerPositionEnum not null,
    goal_nb INTEGER NULL,
    id_team int not null,
    CONSTRAINT fk_team foreign key (id_team) references Team (id)
);
create table Team (
    id  SERIAL primary key,
    name varchar (255),
    continent ContinentEnum not null
);

UPDATE Player SET goal_nb = 0 WHERE name = 'Thibaut Courtois';
UPDATE Player SET goal_nb = 2 WHERE name = 'Dani Carvajal';
UPDATE Player SET goal_nb = 5 WHERE name = 'Jude Bellingham';
UPDATE Player SET goal_nb = NULL WHERE name = 'Robert Lewandowski';
UPDATE Player SET goal_nb = NULL WHERE name = 'Antoine Griezmann';

-- Ajouter une équipe
INSERT INTO Team (id, name, continent) VALUES (2, 'RealMadrid', 'EUROPA');

-- Ajouter quelques joueurs
INSERT INTO Player (name, age, position, id_team, goal_nb) VALUES
                                                               ('Thibaut Courtois', 31, 'GK', 1, 0),
                                                               ('Dani Carvajal', 31, 'DEF', 1, 2),
                                                               ('Jude Bellingham', 20, 'MIDF', 1, 5),
                                                               ('Robert Lewandowski', 35, 'STR', 1, NULL),
                                                               ('Antoine Griezmann', 32, 'STR', 1, NULL);
