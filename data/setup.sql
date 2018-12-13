create extension pgcrypto;

drop table users;

create table users (
  id         serial primary key,
  -- uuid       uuid not null default gen_random_uuid(),
  name       varchar(255),
  email      varchar(255) not null unique,
  password varchar(255) not null
);