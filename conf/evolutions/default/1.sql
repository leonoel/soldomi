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
  primary key (id)
);

create table block (
  id                        bigint not null auto_increment,
  tune_id		    bigint not null,
  primary key (id)
);

# --- !Downs

drop table if exists tune;
drop table if exists syst;
drop table if exists block;
