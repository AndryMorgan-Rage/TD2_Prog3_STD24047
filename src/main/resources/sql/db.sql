CREATE DATABASE mini_football_db;
CREATE USER  mini_football_db_manager WITH PASSWORD 'abc';

GRANT CONNECT ON DATABASE mini_football_db TO mini_football_db_manager;
\c mini_football_db


GRANT USAGE ON SCHEMA public TO mini_football_db_manager;
GRANT CREATE ON SCHEMA public TO mini_football_db_manager;
ALTER DEFAULT PRIVILEGES IN SCHEMA PUBLIC
      GRANT SELECT , INSERT , UPDATE , DELETE ON TABLES TO mini_football_db_manager;
ALTER DEFAULT PRIVILEGES IN SCHEMA PUBLIC
    GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO mini_football_db_manager;