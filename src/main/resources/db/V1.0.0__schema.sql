create table project
(
    id          varchar(5)   not null,
    name        varchar(255) not null,
    description text,
    primary key (id)
);

create table issue_id_generator
(
    project          varchar(5) not null,
    sequence         integer    not null,
    primary key (project),
    foreign key (project) references project(id)
);
