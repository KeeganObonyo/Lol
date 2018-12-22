create extension pgcrypto;

drop table if exists users;

create table users (
  id         serial primary key,
  name       varchar(255),
  email      varchar(255) not null unique,
  password varchar(255) not null
);
