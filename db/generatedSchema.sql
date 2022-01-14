create sequence message_id_seq start 1 increment 1;

    create table Message (
       id int8 not null,
        message varchar(255),
        primary key (id)
    );
