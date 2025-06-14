--liquibase formatted sql

--changeset auto-views:view-v_user runOnChange:true

create or replace view v_user
as
select u.id                                as "id"
     , u.create_date                       as "createDate"
     , u.email                             as "email"
     , u.username                          as "username"
     , u.encrypted_password                as "encryptedPassword"
     , u.first_name                        as "firstName"
     , u.last_name                         as "lastName"
     , u.display_name                      as "displayName"
     , (select fe.id
        from file_entity fe
        where fe.user_id = u.id
          and fe.entity_type = 'USER_PIC') as "userPicId"
from user_entity u
;
