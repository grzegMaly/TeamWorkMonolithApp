use `team-work`;

alter table users
    add column account_non_locked boolean not null default 1;

alter table users
    add column account_non_expired boolean not null default 1;

alter table users
    add column credentials_non_expired boolean not null default 1;

alter table users
    add column credentials_expiry_date date not null;

alter table users
    add column account_expiry_date date not null;