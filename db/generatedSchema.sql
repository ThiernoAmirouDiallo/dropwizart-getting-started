create sequence animal_id_seq start 1 increment 1;
create sequence child_id_seq start 1 increment 1;
create sequence country_id_seq start 1 increment 1;
create sequence department_id_seq start 1 increment 1;
create sequence guide_id_seq start 1 increment 1;
create sequence message_id_seq start 1 increment 1;
create sequence passport_id_seq start 1 increment 1;
create sequence student_id_seq start 1 increment 1;

    create table Animal (
       DTYPE char(1) not null,
        id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table Child (
       id int8 not null,
        name varchar(255),
        firstname_fk varchar(255),
        lastname_fk varchar(255),
        primary key (id)
    );

    create table Country (
       id int8 not null,
        code varchar(255) not null,
        name varchar(255),
        primary key (id)
    );

    create table Department (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table Employee (
       departement_id_fk int8 not null,
        employee_name_cpk_col1 varchar(255) not null,
        name varchar(255),
        primary key (departement_id_fk, employee_name_cpk_col1)
    );

    create table Guide (
       id int8 not null,
        firstName varchar(255),
        lastName varchar(255),
        primary key (id)
    );

    create table Message (
       id int8 not null,
        message varchar(255),
        primary key (id)
    );

    create table Movie (
       id int8 not null,
        releaseDate date,
        title varchar(255),
        primary key (id)
    );

    create table movie_person (
       movie_id int8 not null,
        person_id int8 not null,
        primary key (movie_id, person_id)
    );

    create table Parent (
       firstName varchar(255) not null,
        lastName varchar(255) not null,
        version int4 not null,
        primary key (firstName, lastName)
    );

    create table Passport (
       id int8 not null,
        expirationDate date,
        primary key (id)
    );

    create table Person (
       passport_id int8 not null,
        billing_city varchar(255),
        billing_postalCode varchar(255),
        billing_street varchar(255),
        firstName varchar(255),
        home_city varchar(255),
        home_postalCode varchar(255),
        home_street varchar(255),
        lastName varchar(255),
        sexe varchar(255) not null,
        country_id int8,
        primary key (passport_id)
    );

    create table person_nickname (
       person_id int8 not null,
        nickname varchar(255)
    );

    create table person_nickname2 (
       person_id int8 not null,
        nickname varchar(255),
        nicknames_order int4 not null,
        primary key (person_id, nicknames_order)
    );

    create table person_nickname3 (
       Person_passport_id int8 not null,
        nickname_description varchar(255),
        nickname_key varchar(255) not null,
        primary key (Person_passport_id, nickname_key)
    );

    create table Student (
       id int8 not null,
        name varchar(255),
        guide_id int8,
        primary key (id)
    );

    alter table Country 
       add constraint UK_qyh4l70f9l5k5jcv876rb4j89 unique (code);

    alter table person_nickname 
       add constraint UKpvrermt7njldk4r3s2agf0e7q unique (person_id, nickname);

    alter table person_nickname2 
       add constraint UKoj5pgow5t70gxul1hhu4t37rb unique (person_id, nickname);

    alter table Child 
       add constraint FK30gejy1uwly6b5xu73ul5i5jt 
       foreign key (firstname_fk, lastname_fk) 
       references Parent;

    alter table Employee 
       add constraint FK28af9mbm1bxxrli1tcdt2xwoh 
       foreign key (departement_id_fk) 
       references Department;

    alter table movie_person 
       add constraint FKdhj2xkymhw5hqo93x2ukw6g58 
       foreign key (person_id) 
       references Movie;

    alter table movie_person 
       add constraint FKgv12xuc7jrmip6ynxoqa3jq3j 
       foreign key (movie_id) 
       references Person;

    alter table Person 
       add constraint FKat8ftpi85snasl6eciil1nqas 
       foreign key (country_id) 
       references Country;

    alter table Person 
       add constraint FKbkmid3b13tkn6vbxfebweqlp3 
       foreign key (passport_id) 
       references Passport;

    alter table person_nickname 
       add constraint FKgplq5ckb8rjdw4i4kqtpj8wdu 
       foreign key (person_id) 
       references Person;

    alter table person_nickname2 
       add constraint FKrki3u7d1pa6p4uch3kef1e620 
       foreign key (person_id) 
       references Person;

    alter table person_nickname3 
       add constraint FKmcwgys2imx404ga0m6419nema 
       foreign key (Person_passport_id) 
       references Person;

    alter table Student 
       add constraint FKb4ny3l3cnsxbbn601ev8mhlj5 
       foreign key (guide_id) 
       references Guide;
