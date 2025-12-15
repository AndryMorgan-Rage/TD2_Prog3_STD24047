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
    id_team int not null,
    CONSTRAINT fk_team foreign key (id_team) references Team (id)
);
create table Team (
    id  SERIAL primary key,
    name varchar (255),
    continent ContinentEnum not null
);