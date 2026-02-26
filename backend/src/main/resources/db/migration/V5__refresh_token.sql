use `team-work`;

create table `refresh_token_families`
(
    `id`         bigint auto_increment not null,
    `version`    bigint                not null,
    `user_id`    binary(16)            null,
    `created_at` timestamp             not null,
    `expires_at` timestamp             not null,
    `revoked`    boolean               not null default false,
    `revoked_at` timestamp             null,
    constraint `pk_families` primary key (`id`)
);

create index `idx_rtf_user` on `refresh_token_families` (`user_id`);

create table `refresh_tokens`
(
    `id`                   bigint auto_increment not null,
    `version`              bigint                not null,
    `refresh_token_family` bigint                not null,
    `hashed_token`         varchar(255)          not null,
    `revoked`              boolean               not null default false,
    `created_at`           timestamp             not null,
    `expires_at`           timestamp             not null,
    `revoked_at`           timestamp             null,
    `parent_id`            bigint                null,
    `replaced_by_id`       bigint,
    `roles`                JSON                  not null,
    constraint `pk_rt` primary key (`id`)
);

alter table `refresh_tokens`
    add constraint `fk_rt_rtf_id` foreign key (`refresh_token_family`)
        references `refresh_token_families` (`id`) on delete cascade;

create index `idx_rt_family_revoked`
    on `refresh_tokens` (`refresh_token_family`, `revoked`);
