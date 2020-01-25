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


create table issue
(
    project        varchar(5)   not null,
    issue_sequence integer      not null,
    title          varchar(255) not null,
    primary key (project, issue_sequence),
    foreign key (project) references project(id)
);

create table field
(
    id          uuid         not null,
    version     integer      not null,
    name        varchar(255) not null,
    type        varchar(32)  not null,
    description varchar(255),
    required    boolean,
    min_length  integer,
    max_length  integer,
    primary key (id, version)
);

create table last_field
(
  id      uuid    not null,
  version integer not null,
  primary key (id),
  foreign key (id) references field(id)
);

create table field_value
(
  id       uuid       not null,
  field_id uuid       not null,
  project  varchar(7) not null,
  issue    integer,
  value    text,
  primary key (id),
  foreign key (field_id) references field(id),
  foreign key (project)  references project(id),
  foreign key (issue)    references issue(issue_sequence)
);