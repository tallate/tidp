create database if not exists sidp;
use sidp;
drop table if exists idpkey;
create table if not exists idpkey (
  # 36是UUID的长度，应该不会比这更长了吧？
  `id` varchar(36) not null,
  # 状态的取值只有固定的几个，取值少，没必要加索引
  `key_state` char(12) not null default '',
  `created_time` timestamp not null default now(),
  primary key (`id`)
);
