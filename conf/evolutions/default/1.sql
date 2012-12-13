# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table measure (
  id                        bigint not null,
  tune_id                   bigint,
  relative_position         integer,
  beat_value                varchar(13),
  beat_count                integer,
  absolute_position         integer,
  constraint ck_measure_beat_value check (beat_value in ('UNDEFINED','SIXTY_FOURTH','THIRTY_SECOND','SIXTEENTH','EIGHTH','QUARTER','HALF','WHOLE')),
  constraint pk_measure primary key (id))
;

create table segment (
  id                        bigint not null,
  tune_id                   bigint,
  staff_id                  bigint,
  measure_id                bigint,
  rest                      boolean,
  clef                      varchar(9),
  note                      varchar(1),
  octave                    integer,
  accidental                integer,
  dot                       integer,
  absolute_position         integer,
  duration_symbol           varchar(13),
  constraint ck_segment_clef check (clef in ('UNDEFINED','TREBLE','BASS','ALTO','TENOR')),
  constraint ck_segment_note check (note in ('C','D','E','F','G','A','B')),
  constraint ck_segment_duration_symbol check (duration_symbol in ('UNDEFINED','SIXTY_FOURTH','THIRTY_SECOND','SIXTEENTH','EIGHTH','QUARTER','HALF','WHOLE')),
  constraint pk_segment primary key (id))
;

create table staff (
  id                        bigint not null,
  tune_id                   bigint,
  name                      varchar(255),
  constraint pk_staff primary key (id))
;

create table tune (
  id                        bigint not null,
  title                     varchar(255),
  last_modif_at             timestamp,
  constraint pk_tune primary key (id))
;

create sequence measure_seq;

create sequence segment_seq;

create sequence staff_seq;

create sequence tune_seq;

alter table measure add constraint fk_measure_tune_1 foreign key (tune_id) references tune (id) on delete restrict on update restrict;
create index ix_measure_tune_1 on measure (tune_id);
alter table segment add constraint fk_segment_tune_2 foreign key (tune_id) references tune (id) on delete restrict on update restrict;
create index ix_segment_tune_2 on segment (tune_id);
alter table segment add constraint fk_segment_staff_3 foreign key (staff_id) references staff (id) on delete restrict on update restrict;
create index ix_segment_staff_3 on segment (staff_id);
alter table segment add constraint fk_segment_measure_4 foreign key (measure_id) references measure (id) on delete restrict on update restrict;
create index ix_segment_measure_4 on segment (measure_id);
alter table staff add constraint fk_staff_tune_5 foreign key (tune_id) references tune (id) on delete restrict on update restrict;
create index ix_staff_tune_5 on staff (tune_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists measure;

drop table if exists segment;

drop table if exists staff;

drop table if exists tune;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists measure_seq;

drop sequence if exists segment_seq;

drop sequence if exists staff_seq;

drop sequence if exists tune_seq;

