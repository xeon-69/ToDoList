create table users
(
    id bigint primary key generated always as identity,
    username varchar(255),
    password varchar(255)
);

create table tasks
(
    id bigint primary key generated always as identity,
    title text,
    isCompleted boolean,
    priority varchar(20),
    priorityWeight int not null
--     createdAt timestamp,
--     updatedAt timestamp
);