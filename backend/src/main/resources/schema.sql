CREATE TABLE IF NOT EXISTS `roles`
(
    `role_id`       int auto_increment primary key,
    `app_role` varchar(20) not null,
    constraint `uq_role` unique (`app_role`)
);

CREATE TABLE IF NOT EXISTS `users`
(
    `user_id`    binary(16) primary key,
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
    constraint `fk_role` foreign key (`role_id`) references `roles` (`role_id`),
    constraint `uq_user_username` unique (`username`)
);

CREATE TABLE IF NOT EXISTS `addresses`
(
    `id`       bigint primary key auto_increment,
    `street`   varchar(150) not null,
    `city`     varchar(100) not null,
    `country`  varchar(100) not null,
    `zip_code` varchar(20)  not null,
    `district` varchar(100) not null,
    `user_id`  binary(16)   not null,
    constraint `fk_addresses_user`
        foreign key (`user_id`)
            references `users` (`user_id`)
            on delete cascade
);

CREATE TABLE IF NOT EXISTS `contacts`
(
    `id`                   bigint primary key auto_increment,
    `country_calling_code` varchar(3)  not null,
    `phone_number`         varchar(14) not null,
    `email`                varchar(50) not null,
    `user_id`              binary(16)  not null,
    constraint `fk_contacts_user`
        foreign key (`user_id`)
            references `users` (`user_id`)
            on delete cascade
);

CREATE TABLE IF NOT EXISTS `user_storage`
(
    `resource_id`  binary(16) primary key,
    `version`      bigint     not null,
    `user_id`      binary(16) not null,
    `used_bytes`   bigint     not null default 0,
    `quota_bytes`  bigint     not null default 50000000000,
    `root_node_id` binary(16),
    constraint `uq_user_storage_user_id` unique (`user_id`)
);

CREATE TABLE IF NOT EXISTS `file_nodes`
(
    `id`                binary(16) primary key,
    `version`           bigint       not null,
    `name`              varchar(50)  null,
    `parent_id`         binary(16)   null,
    `storage_key`       varchar(36)  null,
    `materialized_path` varchar(255) null,
    `size`              bigint  default 0,
    `sub_tree_size`     bigint  default 0,
    `node_type`         varchar(32)  not null,
    `deleted`           boolean default 0,
    `user_storage_id`   binary(16)   not null,
    constraint `fk_file_nodes_storage`
        foreign key (`user_storage_id`)
            references `user_storage` (`resource_id`)
);

CREATE INDEX `idx_file_nodes_storage`
    ON `file_nodes` (`user_storage_id`);

ALTER TABLE `user_storage`
    ADD CONSTRAINT `fk_user_storage_root`
        FOREIGN KEY (`root_node_id`)
            REFERENCES `file_nodes` (`id`);

CREATE TABLE IF NOT EXISTS `teams`
(
    `team_id`           binary(16) primary key,
    `team_name`         varchar(40) not null,
    `presentation_name` varchar(40) not null,
    `manager_id`        binary(16)  null,
    `active`            boolean     not null default 1,
    `created_by`        varchar(20) not null,
    `updated_by`        varchar(20) null,
    `created_at`        timestamp   not null default current_timestamp,
    `updated_at`        timestamp   not null default current_timestamp on update current_timestamp,
    constraint `fk_team_manager_id`
        foreign key (`manager_id`)
            references `users` (`user_id`)
            on delete set null,
    constraint `uq_team_name` unique (`team_name`)
);

CREATE INDEX `idx_teams_manager`
    ON `teams` (`manager_id`);

CREATE TABLE IF NOT EXISTS `teams_users`
(
    `team_id` binary(16),
    `user_id` binary(16),
    constraint `pk_team_users` primary key (`team_id`, `user_id`),
    constraint `fk_team_users_team` foreign key (`team_id`) references `teams` (`team_id`),
    constraint `fk_team_users_user` foreign key (`user_id`) references `users` (`user_id`)
);

CREATE INDEX `idx_team_users_user`
    ON `teams_users` (`user_id`);