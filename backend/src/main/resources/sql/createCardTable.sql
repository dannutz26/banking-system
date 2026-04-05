create table cards (
    id bigserial primary key,
    card_number varchar(16) not null unique,
    card_holder_name varchar(255) not null,
    cvv varchar(3) not null,
    expiry_date date not null,
    card_type varchar(20) not null,
    is_blocked boolean default false,
    account_id bigint not null,

    constraint fk_card_account
        foreign key(account_id)
            references accounts(id)
            on delete cascade
);

create index idx_card_number on cards(card_number);