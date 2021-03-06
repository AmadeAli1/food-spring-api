create table if not exists categoria
(
    id     integer generated by default as identity
        primary key,
    nome   varchar(30) not null
        unique,
    imagem text        not null
);

create table if not exists usuario
(
    uid      text                  not null
        primary key,
    username varchar(40)           not null,
    password varchar               not null,
    email    varchar               not null
        unique,
    role     varchar               not null,
    enable   boolean default false not null,
    imageurl text
);


create table if not exists token
(
    id          text      not null
        primary key,
    usuario_id  text      not null
        unique
        references usuario
            on update cascade on delete cascade,
    createdat   timestamp not null,
    expiredat   timestamp not null,
    confirmedat timestamp
);

create table if not exists image
(
    id       integer generated by default as identity
        primary key,
    image    bytea   not null,
    filetype varchar not null,
    filename varchar not null
);

create table if not exists produto
(
    id          integer generated by default as identity
        primary key,
    nome        varchar(50)       not null,
    description varchar(255)      not null,
    preco       double precision  not null,
    quantidade  integer           not null,
    createdat   timestamp         not null,
    likes       integer default 0 not null,
    category_id integer           not null
        references categoria
            on update cascade on delete cascade
);

create table if not exists usuario_produto
(
    id         integer generated by default as identity
        primary key,
    produto_id integer not null
        references produto
            on update cascade on delete cascade,
    usuario_id text    not null
        references usuario
            on update cascade on delete cascade
);

create table if not exists shopping_cart
(
    id         integer generated by default as identity
        primary key,
    quantidade integer     not null,
    produto_id integer     not null
        references produto
            on update cascade on delete cascade,
    usuario_id text        not null
        references usuario
            on update cascade on delete cascade,
    status     varchar(20) not null
);

create table if not exists produto_imagem
(
    produto_id integer not null
        references produto
            on update cascade on delete cascade,
    imagem     text    not null
);