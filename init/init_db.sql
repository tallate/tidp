create database if not exists sidp;
use sidp;
drop table if exists idpkey;
create table if not exists idpkey (
  `id` varchar(36) not null,
  `key_state` char(12) not null default '',
  `created_time` timestamp not null default now(),
  primary key (`id`)
);
