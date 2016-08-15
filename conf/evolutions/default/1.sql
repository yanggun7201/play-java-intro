# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table tb_person (
  person_id                 bigint auto_increment not null,
  person_name               varchar(255),
  person_nick               varchar(255),
  complete_flag             boolean,
  reg_date                  timestamp,
  constraint pk_tb_person primary key (person_id))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists tb_person;

SET REFERENTIAL_INTEGRITY TRUE;

