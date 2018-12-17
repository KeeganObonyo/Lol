drop extension if exists pgcrypto;

create extension pgcrypto;

drop database if exists lol_database;

create database lol_database;

drop table if exists users;

create table users (
  id         serial primary key,
  -- uuid       uuid not null default gen_random_uuid(),
  name       varchar(255),
  email      varchar(255) not null unique,
  password varchar(255) not null
);