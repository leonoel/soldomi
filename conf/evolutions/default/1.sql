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
  foreign key (tune_id)     references tune(id),
  primary key (id)
);

create table staff (
  id                        bigint not null auto_increment,
  syst_id                   bigint not null,
  name                      varchar(255),
  foreign key (syst_id)     references syst(id),
  primary key (id)
);

create table sect (
  id                        bigint not null auto_increment,
  tune_id                   bigint not null,
  start_time                bigint not null,
  foreign key (tune_id)	    references tune(id),
  primary key (id)
);

create table block (
  id                        bigint not null auto_increment,
  sect_id		    bigint not null,
  start_time                bigint,
  foreign key (sect_id)	    references sect(id),
  primary key (id)
);

create table symbol (
  id                        bigint not null auto_increment,
  staff_id                  bigint not null,
  block_id                  bigint not null,
  start_time_numerator      int,
  start_time_denominator    int not null,
  symbol_type               int not null,
  foreign key (staff_id)    references staff(id),
  foreign key (block_id)    references block(id),
  primary key (id)
);

create table segment (
  id                        bigint not null auto_increment,
  symbol_id                 bigint not null,
  duration_numerator        int,
  duration_denominator      int not null,
  dot_count                 int,
  tuplet_id                 bigint,
  foreign key (symbol_id)   references symbol(id),
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
  note_name                 int not null,
  octave                    int not null,
  accidental		    int not null,
  foreign key (segment_id)  references segment(id),
  primary key (id)
);

create table time_signature (
  id                        bigint not null auto_increment,
  symbol_id                 bigint not null,
  beat_count                int,
  beat_value                int,
  foreign key (symbol_id)   references symbol(id),
  primary key (id)
);

create table key_signature (
  id                        bigint not null auto_increment,
  symbol_id                 bigint not null,
  a                         int not null,
  b                         int not null,
  c                         int not null,
  d                         int not null,
  e                         int not null,
  f                         int not null,
  g                         int not null,
  foreign key (symbol_id)   references symbol(id),
  primary key (id)
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
