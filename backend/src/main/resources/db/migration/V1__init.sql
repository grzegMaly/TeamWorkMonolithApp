use `team-work`;

CREATE TABLE IF NOT EXISTS `addresses`
(
    `id`       bigint auto_increment not null,
    `street`   varchar(150)          not null,
    `city`     varchar(100)          not null,
    `country`  varchar(100)          not null,
    `zip_code` varchar(20)           not null,
    `district` varchar(100)          not null,
    `user_id`  binary(16)            null,
    constraint `pk_addresses` primary key (`id`)
);

CREATE TABLE IF NOT EXISTS `contacts`
(
    `id`                   bigint auto_increment not null,
    `country_calling_code` varchar(3)            not null,
    `phone_number`         varchar(14)           not null,
    `email`                varchar(50)           not null,
    `user_id`              binary(16)            not null,
    constraint `pk_contacts` primary key (`id`)
);

CREATE TABLE IF NOT EXISTS `file_nodes`
(
    `id`                binary(16)   not null,
    `version`           bigint       null,
    `name`              varchar(50)  null,
    `parent_id`         binary(16)   null,
    `storage_key`       varchar(36)  null,
    `materialized_path` varchar(255) null,
    `size`              bigint       null     default 0,
    `sub_tree_size`     bigint       null     default 0,
    `node_type`         varchar(32)  not null,
    `deleted`           boolean      not null default 0,
    `user_storage_id`   binary(16)   null,
    constraint `pk_file_nodes` primary key (`id`)
);

CREATE TABLE IF NOT EXISTS `roles`
(
    `role_id`  int auto_increment not null,
    `app_role` varchar(20)        not null,
    constraint `pk_roles` primary key (`role_id`)
);

CREATE TABLE IF NOT EXISTS `teams`
(
    `team_id`           binary(16)  not null,
    `team_name`         varchar(40) not null,
    `presentation_name` varchar(40) not null,
    `manager_id`        binary(16)  null,
    `active`            boolean     not null default 1,
    `created_by`        varchar(20) not null,
    `updated_by`        varchar(20) null,
    `created_at`        timestamp   not null default current_timestamp,
    `updated_at`        timestamp   not null default current_timestamp on update current_timestamp,
    constraint `pk_teams` primary key (`team_id`)
);

CREATE TABLE IF NOT EXISTS `teams_users`
(
    `team_id` binary(16) not null,
    `user_id` binary(16) not null,
    constraint `pk_team_users` primary key (`team_id`, `user_id`)
);

CREATE TABLE IF NOT EXISTS `user_storage`
(
    `resource_id`  binary(16) not null,
    `version`      bigint     not null,
    `user_id`      binary(16) not null,
    `used_bytes`   bigint     not null default 0,
    `quota_bytes`  bigint     not null default 50000000000,
    `root_node_id` binary(16) null,
    constraint `pk_users_storage` primary key (`resource_id`)
);

CREATE TABLE IF NOT EXISTS `users`
(
    `user_id`    binary(16)  not null,
    `first_name` varchar(20) not null,
    `last_name`  varchar(20) not null,
    `username`   varchar(20) not null,
    `password`   varchar(60) not null,
    `image_key`  varchar(50) not null default 'defaultProfileImage',
    `deleted`    boolean     not null default 0,
    `role_id`    int         not null,
    `created_by` varchar(20) not null,
    `updated_by` varchar(20) null,
    `created_at` timestamp   not null default current_timestamp,
    `updated_at` timestamp   not null default current_timestamp on update current_timestamp,
    constraint `pk_users` primary key (`user_id`)
);

alter table addresses
    add constraint `fk_addresses_user`
        foreign key (`user_id`)
            references `users` (`user_id`)
            on delete cascade;

alter table contacts
    add constraint `fk_contacts_user`
        foreign key (`user_id`)
            references `users` (`user_id`)
            on delete cascade;

alter table contacts
    add constraint `uq_contacts_user` unique (`user_id`);

alter table file_nodes
    add constraint `fk_file_nodes_storage`
        foreign key (`user_storage_id`)
            references `user_storage` (`resource_id`);

create index `idx_file_nodes_storage` on `file_nodes` (`user_storage_id`);

alter table roles
    add constraint `uq_role` unique (`app_role`);

alter table teams
    add constraint `fk_team_manager_id`
        foreign key (`manager_id`)
            references `users` (`user_id`)
            on delete set null;

alter table teams
    add constraint `uq_team_name` unique (`team_name`);

create index `idx_teams_manager` on `teams` (`manager_id`);

alter table teams_users
    add constraint `fk_team_users_team` foreign key (`team_id`) references `teams` (`team_id`);

alter table teams_users
    add constraint `fk_team_users_user` foreign key (`user_id`) references `users` (`user_id`);

create index `idx_team_users_user` on `teams_users` (`user_id`);

alter table user_storage
    add constraint `uq_user_storage_root_id` unique (`root_node_id`);

alter table user_storage
    add constraint `uq_user_storage_user_id` unique (`user_id`);

alter table user_storage
    add constraint `fk_user_storage_root_node_id` foreign key (`root_node_id`) references `file_nodes` (`id`);

create unique index `idx_user_storage_user_id` on `user_storage` (`user_id`);

alter table users
    add constraint `fk_role` foreign key (`role_id`) references `roles` (`role_id`);

alter table users
    add constraint `uq_user_username` unique (`username`);

create unique index `idx_user_username` on `users` (`username`);