--liquibase formatted sql

--changeset auto-views:view-v_task runOnChange:true

create or replace view v_task as
select t.id            as "id"
     , t.create_date   as "createDate"
     , t.name          as "name"
     , t.description   as "description"
     , t.project_id    as "projectId"
     , t.column_id     as "columnId"
     , t.owner_id      as "ownerId"
     , t.assignee_id   as "assigneeId"
     , t.priority      as "priority"
     , t.task_type_id  as "taskTypeId"
     , count(fe.id)    as "filesCounter"
     , t.update_date   as "updateDate"
     , t.entity_order  as "entityOrder"
     , t.deadline_date as "deadlineDate"
from task t
         left join file_entity fe
                   on fe.entity_id = t.id
                       and fe.entity_type = 'TASK'
group by t.id,
         t.create_date,
         t.name,
         t.description,
         t.project_id,
         t.column_id,
         t.owner_id,
         t.assignee_id,
         t.priority,
         t.task_type_id,
         t.update_date,
         t.entity_order,
         t.deadline_date;

