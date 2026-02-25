use `team-work`;

create table `refresh_tokens`
(
    `id`                bigint auto_increment not null,
    `version`           bigint                not null,
    `user_id`           binary(16)            not null,
    `hashed_token`      varchar(255)          not null,
    `revoked`           boolean               not null,
    `created_at`        timestamp             not null,
    `expires_at`        timestamp             not null,
    `revoked_at`        timestamp             null,
    `parent_id`         bigint                null,
    `family_id`         binary(16)            not null,
    `family_expires_at` timestamp             not null,
    `replaced_by_id`    bigint,
    `roles`             JSON                  not null,
    constraint `pk_rt` primary key (`id`)
);

create index `idx_rt_user` on `refresh_tokens` (`user_id`);
create index `idx_rt_family` on `refresh_tokens` (`user_id`);
create index `idx_rt_revoked` on `refresh_tokens` (`revoked`);
