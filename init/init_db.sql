create database if not exists tidp;
use tidp;

drop table if exists idpkey;
create table if not exists idpkey
(
  `id`           varchar(36) not null,
  `key_state`    char(12)    not null default '',
  `created_time` timestamp   not null default now(),
  `content`      blob        null,
  primary key (`id`)
);
