package com.febfes.fftmback.domain.common.specification;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.JoinFetch;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@JoinFetch(paths = "taskList", alias = "t")
@JoinFetch(paths = "t.owner", alias = "own")
@JoinFetch(paths = "t.assignee", alias = "asg")
@JoinFetch(paths = "t.taskType", alias = "tt")
@And({
        @Spec(path = "t.id", params = "taskId", spec = Equal.class),
        @Spec(path = "t.name", params = "taskName", spec = LikeIgnoreCase.class),
        @Spec(path = "t.description", params = "taskDescription", spec = LikeIgnoreCase.class),
        @Spec(path = "own.id", params = "taskOwnerId", spec = Equal.class),
        @Spec(path = "asg.id", params = "taskAssigneeId", spec = Equal.class),
        @Spec(path = "t.priority", params = "taskPriority", spec = EqualIgnoreCase.class),
        @Spec(path = "tt.name", params = "taskType", spec = Equal.class)
})
public interface ColumnWithTasksSpec extends Specification<TaskColumnEntity> {

    static Specification<TaskColumnEntity> byProjectId(Long projectId) {
        return (root, query, builder) -> builder.equal(root.get("projectId"), projectId);
    }
}
