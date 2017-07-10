# Users table

# --- !Ups

CREATE TABLE users (
    uuid UUID NOT NULL,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    PRIMARY KEY (uuid)
);

# --- !Downs

DROP TABLE users;