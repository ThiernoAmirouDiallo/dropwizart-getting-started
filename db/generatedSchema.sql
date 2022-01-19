create sequence message_id_seq start 1 increment 1;
create sequence person_id_seq start 1 increment 1;

    create table Country (
       id varchar(255) not null,
        name varchar(255),
        primary key (id)
    );

    create table Message (
       id int8 not null,
        message varchar(255),
        primary key (id)
    );

    create table Person (
       id int8 not null,
        billing_city varchar(255),
        billing_postalCode varchar(255),
        billing_street varchar(255),
        firstName varchar(255),
        home_city varchar(255),
        home_postalCode varchar(255),
        home_street varchar(255),
        lastName varchar(255),
        country_id varchar(255),
        primary key (id)
    );

    alter table Person 
       add constraint FKat8ftpi85snasl6eciil1nqas 
       foreign key (country_id) 
       references Country;
