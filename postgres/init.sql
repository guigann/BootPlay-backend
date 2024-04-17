CREATE USER bootcamp SUPERUSER PASSWORD 'bootcamppass';
CREATE DATABASE "bootcamp"
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.utf8'
       LC_CTYPE = 'en_US.utf8'
       CONNECTION LIMIT = -1;
GRANT ALL PRIVILEGES ON DATABASE bootcamp TO bootcamp;