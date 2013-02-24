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
  start_time                bigint,
  symbol_type               varchar(255),
  primary key (id)
);

create table segment (
  id                        bigint not null auto_increment,
  segment_id                bigint not null,
  duration_type             varchar(255),
  dot_count                 int,
  pitch_note                varchar(255),
  pitch_octave              int,
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
