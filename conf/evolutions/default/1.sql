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

# --- !Downs

drop table if exists tune;
drop table if exists syst;
drop table if exists staff;
drop table if exists sect;
drop table if exists block;
