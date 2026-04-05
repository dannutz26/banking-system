create table persistent_logins (
    id serial primary key,
    username varchar(100) not null,
    series varchar(64) unique not null,
    token varchar(64) not null,
    last_used timestamp not null
);