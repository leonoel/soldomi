# --- !Ups

create table tune (
  id                        bigint not null auto_increment,
  name                      varchar(255),
  last_modified             timestamp,
  primary key (id)
);

create table syst (
  id                        bigint not null auto_increment,
  tune_id                   bigint not null,
  name                      varchar(255),
  primary key (id)
);

create table staff (
  id                        bigint not null auto_increment,
  syst_id                   bigint not null,
  name                      varchar(255),
  primary key (id)
);

create table sect (
  id                        bigint not null auto_increment,
  tune_id                   bigint not null,
  start_time                bigint not null,
  primary key (id)
);

create table block (
  id                        bigint not null auto_increment,
  sect_id		    bigint not null,
  start_time                bigint,
  primary key (id)
);

create table symbol (
  id                        bigint not null auto_increment,
  staff_id                  bigint not null,
  block_id                  bigint not null,
  start_time_n              int,
  start_time_d              int not null,
  symbol_type               varchar(255),
  primary key (id)
);

create table segment (
  id                        bigint not null auto_increment,
  symbol_id                 bigint not null,
  duration_n                int,
  duration_d                int not null,
  dot_count                 int,
  tuplet_id                 bigint,
  primary key (id)
);

create table tuplet (
  id                        bigint not null auto_increment,
  duration                  bigint,
  primary key (id)
);

create table note (
  id                        bigint not null auto_increment,
  segment_id                bigint not null,
  note_name                 varchar(255),
  octave                    int,
  accidental		    varchar(255),
  primary key (id)
);

create table time_signature (
  id                        bigint not null auto_increment,
  symbol_id                 bigint not null,
  beat_count                int,
  beat_value                varchar(255),
  primary key (id)
);

create table key_signature (
  id                        bigint not null auto_increment,
  symbol_id                 bigint not null,
  a                         varchar(255),
  b                         varchar(255),
  c                         varchar(255),
  d                         varchar(255),
  e                         varchar(255),
  f                         varchar(255),
  g                         varchar(255),
  primary key(id)
);

create table preset (
  id                        bigint not null auto_increment,
  name                      varchar(255),
  js                        varchar(1000000),
  primary key (id)
);

# --- !Downs

drop table if exists tune;
drop table if exists syst;
drop table if exists staff;
drop table if exists sect;
drop table if exists block;
drop table if exists symbol;
drop table if exists segment;
drop table if exists note;
drop table if exists tuplet;
drop table if exists time_signature;
drop table if exists key_signature;
drop table if exists preset;
