--liquibase formatted sql

--changeset auto-views:view-v_task runOnChange:true

create or replace view v_task
as
select t.id                            as "id"
     , t.create_date                   as "createDate"
     , t.name                          as "name"
     , t.description                   as "description"
     , t.project_id                    as "projectId"
     , t.column_id                     as "columnId"
     , t.owner_id                      as "ownerId"
     , t.assignee_id                   as "assigneeId"
     , t.priority                      as "priority"
     , t.task_type_id                  as "taskTypeId"
     , (select count(*)
        from file_entity fe
        where fe.entity_id = t.id
          and fe.entity_type = 'TASK') as "filesCounter"
     , t.update_date                   as "updateDate"
     , t.entity_order                  as "entityOrder"
from task t
;
